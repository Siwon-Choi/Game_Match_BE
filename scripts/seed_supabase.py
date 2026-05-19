import os
import random
import string
import subprocess
import tempfile
from collections import defaultdict
from datetime import date, datetime, timedelta
from pathlib import Path

from faker import Faker

PROJECT_ROOT = Path(__file__).resolve().parents[1]
RUNNER_SOURCE = PROJECT_ROOT / "scripts" / "JdbcSqlRunner.java"
BCrypt_SOURCE = PROJECT_ROOT / "scripts" / "BCryptHashTool.java"
DEFAULT_PASSWORD_HASH = "$2a$10$cnJb/JM1/yh9Y.jGvZKi6ujHfJBoMOlHaX0nvR6r1zLKSaVPR4Oey"

fake = Faker("ko_KR")

EMAIL_DOMAINS = ["naver.com", "gmail.com", "daum.net", "hanmail.net", "nate.com"]

GAMES = [
    ("던전앤파이터", "RPG", "던전앤파이터_로고"),
    ("FC 온라인", "스포츠", "FC_온라인_로고"),
    ("메이플스토리", "RPG", "메이플스토리_로고"),
    ("프라시아 전기", "RPG", "프라시아_전기_로고"),
    ("메이플스토리M", "RPG", "메이플스토리M_로고"),
    ("히트2", "RPG", "히트2_로고"),
    ("FC 모바일", "스포츠", "FC_모바일_로고"),
    ("블루 아카이브", "RPG", "블루_아카이브_로고"),
    ("던전앤파이터 모바일", "RPG", "던전앤파이터_모바일_로고"),
    ("마비노기", "기타", "마비노기_로고"),
    ("데이브 더 다이버", "기타", "데이브_더_다이버_로고"),
    ("서든어택", "액션", "서든어택_로고"),
    ("바람의나라: 연", "RPG", "바람의나라_연_로고"),
    ("카운터-스트라이크 온라인", "액션", "카운터-스트라이크_온라인_로고"),
    ("마비노기 영웅전", "RPG", "마비노기_영웅전_로고"),
    ("카트라이더 러쉬플러스", "레이싱", "카트라이더_러쉬플러스_로고"),
    ("엘소드", "기타", "엘소드_로고"),
    ("진·삼국무쌍 M", "액션", "진·삼국무쌍_M_로고"),
    ("V4", "RPG", "V4_로고"),
    ("메이플스토리 월드", "기타", "메이플스토리_월드_로고"),
    ("어둠의전설", "RPG", "어둠의전설_로고"),
    ("빌딩앤파이터", "액션", "빌딩앤파이터_로고"),
    ("테일즈위버", "RPG", "테일즈위버_로고"),
    ("바람의나라", "RPG", "바람의나라_로고"),
    ("크레이지 아케이드", "기타", "크레이지_아케이드_로고"),
    ("나이트 워커", "기타", "나이트_워커_로고"),
    ("사이퍼즈", "전략", "사이퍼즈_로고"),
    ("아스가르드", "기타", "아스가르드_로고"),
    ("클로저스", "기타", "클로저스_로고"),
    ("워헤이븐", "기타", "워헤이븐_로고"),
    ("카트라이더: 드리프트", "레이싱", "카트라이더_드리프트_로고"),
    ("던전앤파이터 듀얼", "기타", "던전앤파이터_듀얼_로고"),
    ("버블파이터", "기타", "버블파이터_로고"),
    ("일랜시아", "RPG", "일랜시아_로고"),
    ("고질라 디펜스 포스", "전략", "고질라_디펜스_포스_로고"),
    ("메이플스토리2", "RPG", "메이플스토리2_로고"),
    ("THE FINALS", "기타", "THE_FINALS_로고"),
    ("넥슨타운: NEXONTOWN", "기타", "넥슨타운_NEXONTOWN_로고"),
    ("퍼스트 버서커: 카잔", "기타", "퍼스트_버서커_카잔_로고"),
    ("퍼스트 디센던트", "기타", "퍼스트_디센던트_로고"),
    ("테일즈런너", "기타", "테일즈런너_로고"),
    ("파이널판타지14", "RPG", "파이널판타지14_로고"),
    ("프리스타일2", "스포츠", "프리스타일2_로고"),
    ("아키에이지", "RPG", "아키에이지_로고"),
    ("드래곤네스트", "RPG", "드래곤네스트_로고"),
]


def load_env_file(path):
    if not path.exists():
        return

    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        os.environ.setdefault(key.strip(), value.strip().strip('"').strip("'"))


def env_int(name, default):
    value = os.getenv(name)
    return default if value is None or value == "" else int(value)


def env_float(name, default):
    value = os.getenv(name)
    return default if value is None or value == "" else float(value)


def sql_value(value):
    if value is None:
        return "null"
    if isinstance(value, bool):
        return "true" if value else "false"
    if isinstance(value, (int, float)):
        return str(value)
    if isinstance(value, datetime):
        value = value.isoformat(sep=" ")
    elif isinstance(value, date):
        value = value.isoformat()

    text = str(value).replace("\x00", "").replace(";", ",")
    return "'" + text.replace("'", "''") + "'"


def q_identifier(identifier):
    return '"' + identifier.replace('"', '""') + '"'


def insert_rows(table, columns, rows, batch_size=1000):
    if not rows:
        return []

    column_sql = ", ".join(q_identifier(column) for column in columns)
    statements = []
    for start in range(0, len(rows), batch_size):
        batch = rows[start:start + batch_size]
        values_sql = []
        for row in batch:
            values_sql.append("(" + ", ".join(sql_value(value) for value in row) + ")")
        statements.append(f"insert into {q_identifier(table)} ({column_sql}) values\n" + ",\n".join(values_sql) + ";")
    return statements


def reset_sequences(tables):
    statements = []
    for table in tables:
        statements.append(
            "select setval(pg_get_serial_sequence("
            + sql_value(q_identifier(table))
            + ", 'id'), greatest(coalesce((select max(\"id\") from "
            + q_identifier(table)
            + "), 1), 1), true);"
        )
    return statements


def find_spring_security_crypto_jar():
    candidates = sorted(
        Path.home().glob(
            ".gradle/caches/modules-2/files-2.1/org.springframework.security/"
            "spring-security-crypto/*/*/spring-security-crypto-*.jar"
        ),
        reverse=True,
    )

    for candidate in candidates:
        if candidate.exists() and not candidate.name.endswith("-sources.jar"):
            return candidate

    raise RuntimeError("spring-security-crypto jar를 찾지 못했습니다. 먼저 ./gradlew compileJava를 실행해주세요.")


def find_spring_jcl_jar():
    candidates = sorted(
        Path.home().glob(".gradle/caches/modules-2/files-2.1/org.springframework/spring-jcl/*/*/spring-jcl-*.jar"),
        reverse=True,
    )

    for candidate in candidates:
        if candidate.exists() and not candidate.name.endswith("-sources.jar"):
            return candidate

    raise RuntimeError("spring-jcl jar를 찾지 못했습니다. 먼저 ./gradlew compileJava를 실행해주세요.")


def bcrypt_hash_passwords(passwords, strength):
    if not passwords:
        return []

    crypto_jar = find_spring_security_crypto_jar()
    spring_jcl_jar = find_spring_jcl_jar()
    with tempfile.TemporaryDirectory(prefix="game-match-bcrypt-") as tmp:
        tmp_path = Path(tmp)
        class_dir = tmp_path / "classes"
        class_dir.mkdir()
        classpath = f"{crypto_jar}:{spring_jcl_jar}"

        subprocess.run(["javac", "-cp", classpath, "-d", str(class_dir), str(BCrypt_SOURCE)], check=True)
        process = subprocess.run(
            ["java", "-cp", f"{class_dir}:{classpath}", "BCryptHashTool", str(strength)],
            input="\n".join(passwords) + "\n",
            text=True,
            capture_output=True,
            check=True,
        )

    hashes = process.stdout.splitlines()
    if len(hashes) != len(passwords):
        raise RuntimeError("생성된 BCrypt hash 개수가 요청한 비밀번호 개수와 다릅니다.")

    return hashes


def korean_word(length=2):
    return "".join(chr(random.randint(0xAC00, 0xD7A3)) for _ in range(length))


def unique_phone(existing):
    while True:
        phone = f"010-{random.randint(0, 9999):04d}-{random.randint(0, 9999):04d}"
        if phone not in existing:
            existing.add(phone)
            return phone


def unique_login_id(existing):
    while True:
        login_id = "".join(random.choices(string.ascii_lowercase, k=random.randint(4, 8)))
        login_id += "".join(random.choices(string.digits, k=3))
        if login_id not in existing:
            existing.add(login_id)
            return login_id


def random_birth():
    year = min(2010, max(1980, int(random.gauss(1996, 6))))
    return date(year, random.randint(1, 12), random.randint(1, 28))


def random_seed_date(start_days=-365, end_days=0):
    return fake.date_between(
        start_date=date.today() + timedelta(days=start_days),
        end_date=date.today() + timedelta(days=end_days),
    )


def build_seed_data():
    random.seed(env_int("SEED_RANDOM_SEED", 20260511))
    fake.seed_instance(env_int("SEED_RANDOM_SEED", 20260511))
    fake.unique.clear()

    user_count = env_int("SEED_USER_COUNT", 10_000)
    groups_per_game = env_int("SEED_GROUPS_PER_GAME", 10)
    max_games_per_user = env_int("SEED_MAX_GAMES_PER_USER", 5)
    group_join_rate = env_float("SEED_GROUP_JOIN_RATE", 0.7)
    posts_per_game = env_int("SEED_POSTS_PER_GAME", 100)
    max_root_comments = env_int("SEED_MAX_ROOT_COMMENTS_PER_POST", 10)
    max_replies = env_int("SEED_MAX_REPLIES_PER_COMMENT", 5)
    friends_per_game_user = env_int("SEED_FRIENDS_PER_GAME_USER", 10)
    matches_per_host_max = env_int("SEED_MATCHES_PER_HOST_MAX", 3)
    match_start_days = env_int("SEED_MATCH_START_DAYS", -3)
    match_window_days = env_int("SEED_MATCH_WINDOW_DAYS", 6)
    team_match_rate = env_float("SEED_TEAM_MATCH_RATE", 0.8)
    solo_host_participation_rate = env_float("SEED_SOLO_HOST_PARTICIPATION_RATE", 0.8)
    team_host_participation_rate = env_float("SEED_TEAM_HOST_PARTICIPATION_RATE", 0.8)
    solo_approve_rate_under_recruit = env_float("SEED_SOLO_APPROVE_RATE_UNDER_RECRUIT", 0.5)
    solo_approve_rate_over_recruit = env_float("SEED_SOLO_APPROVE_RATE_OVER_RECRUIT", 0.7)
    solo_await_rate = env_float("SEED_SOLO_AWAIT_RATE", 0.9)
    requests_per_team_match_max = env_int("SEED_REQUESTS_PER_TEAM_MATCH_MAX", 5)
    team_approve_rate = env_float("SEED_TEAM_APPROVE_RATE", 0.2)
    recommendations_per_post_max = env_int("SEED_RECOMMENDATIONS_PER_POST_MAX", 0)
    test_user_count = min(env_int("SEED_TEST_USER_COUNT", 10), user_count)
    test_password_hash = os.getenv("SEED_TEST_USER_PASSWORD_HASH", DEFAULT_PASSWORD_HASH)
    generated_password_pattern = os.getenv("SEED_GENERATED_PASSWORD_PATTERN", "seed-user-{user_id}!")
    generated_password_strength = env_int("SEED_GENERATED_PASSWORD_BCRYPT_STRENGTH", 6)

    generated_passwords = [
        generated_password_pattern.format(user_id=user_id)
        for user_id in range(test_user_count + 1, user_count + 1)
    ]
    generated_password_hashes = bcrypt_hash_passwords(generated_passwords, generated_password_strength)

    games = [(index, name, sort, url) for index, (name, sort, url) in enumerate(GAMES, start=1)]

    def seed_birth():
        year = min(2010, max(1980, int(random.gauss(1995, 6))))
        return date(year, random.randint(1, 12), random.randint(1, 28))

    def seed_login_id(existing):
        while True:
            login_id = "".join(random.choices(string.ascii_lowercase, k=random.randint(3, 7)))
            login_id += "".join(random.choices(string.digits, k=random.randint(2, 3)))
            if login_id not in existing:
                existing.add(login_id)
                return login_id

    login_ids = set()
    emails = set()
    phones = set()
    users = []
    for user_id in range(1, user_count + 1):
        if user_id <= test_user_count:
            email = f"testuser{user_id}@example.com"
            login_id = f"testuser{user_id}"
            name = f"테스트유저{user_id}"
            password_hash = test_password_hash
            emails.add(email)
            login_ids.add(login_id)
        else:
            while True:
                email = f"{fake.unique.user_name()}@{random.choice(EMAIL_DOMAINS)}"
                if email not in emails:
                    emails.add(email)
                    break

            login_id = seed_login_id(login_ids)
            name = fake.name()
            password_hash = generated_password_hashes[user_id - test_user_count - 1]

        users.append((
            user_id,
            name,
            email,
            None,
            unique_phone(phones),
            seed_birth(),
            login_id,
            password_hash,
        ))

    groups = []
    groups_by_game = defaultdict(list)
    group_id = 1
    for game_id, *_ in games:
        for _ in range(groups_per_game):
            group_name = random.choice([f"{korean_word(2)} {fake.word()}", fake.word()])
            groups.append((group_id, game_id, group_name[:255]))
            groups_by_game[game_id].append(group_id)
            group_id += 1

    game_users = []
    game_user_by_id = {}
    game_users_by_game = defaultdict(list)
    game_users_by_group = defaultdict(list)
    nickname_keys = set()
    game_user_id = 1
    game_ids = [game[0] for game in games]
    for user_id, *_ in users:
        count = random.randint(0, min(max_games_per_user, len(game_ids)))
        for game_id in random.sample(game_ids, count):
            for _ in range(10):
                nickname = f"{korean_word(3)}{fake.word()}"[:255]
                nickname_key = (nickname, game_id)
                if nickname_key not in nickname_keys:
                    nickname_keys.add(nickname_key)
                    break
            else:
                continue

            eligible_groups = groups_by_game.get(game_id, [])
            group_id = random.choice(eligible_groups) if eligible_groups and random.random() < group_join_rate else None
            game_users.append((game_user_id, user_id, game_id, group_id, nickname))
            game_user_by_id[game_user_id] = {
                "user_id": user_id,
                "game_id": game_id,
                "group_id": group_id,
            }
            game_users_by_game[game_id].append(game_user_id)
            if group_id is not None:
                game_users_by_group[group_id].append(game_user_id)
            game_user_id += 1

    posts = []
    post_id = 1
    user_ids = [user[0] for user in users]
    for game_id, *_ in games:
        for _ in range(posts_per_game):
            posts.append((
                post_id,
                fake.sentence(nb_words=4)[:15],
                random_seed_date(-365, 0),
                random.randint(0, 1_000),
                random.randint(0, 500),
                random.randint(0, 100),
                fake.text(max_nb_chars=45),
                random.choice(user_ids),
                game_id,
                fake.time_object().replace(microsecond=0),
                random.choice([True, False]),
            ))
            post_id += 1

    comments = []
    comment_id = 1
    for post in posts:
        current_post_id = post[0]
        post_date = post[2]
        post_time = post[9]
        root_count = random.randint(1, max_root_comments)

        for _ in range(root_count):
            base_dt = datetime.combine(post_date, post_time) + timedelta(minutes=random.randint(1, 1440))
            comments.append((
                comment_id,
                random.choice(user_ids),
                fake.text(max_nb_chars=45),
                base_dt.date(),
                base_dt.time().replace(microsecond=0),
                current_post_id,
                None,
                random.choice([True, False]),
            ))
            root_id = comment_id
            comment_id += 1

            for _ in range(random.randint(0, max_replies)):
                reply_dt = base_dt + timedelta(minutes=random.randint(1, 1440))
                comments.append((
                    comment_id,
                    random.choice(user_ids),
                    fake.text(max_nb_chars=45),
                    reply_dt.date(),
                    reply_dt.time().replace(microsecond=0),
                    current_post_id,
                    root_id,
                    random.choice([True, False]),
                ))
                comment_id += 1

    recommendations = []
    recommendation_keys = set()
    if recommendations_per_post_max > 0:
        for post in posts:
            for user_id in random.sample(user_ids, min(random.randint(0, recommendations_per_post_max), len(user_ids))):
                key = (post[0], user_id)
                if key in recommendation_keys:
                    continue
                recommendation_keys.add(key)
                recommendations.append((post[0], user_id, random.choice([True, False])))

    friendships = []
    friendship_keys = set()
    friends_by_game_user = defaultdict(set)
    friendship_id = 1
    for game_user_ids in game_users_by_game.values():
        if len(game_user_ids) < 2:
            continue

        for source_id in game_user_ids:
            partner_count = min(friends_per_game_user, len(game_user_ids) - 1)
            partners = random.sample([candidate for candidate in game_user_ids if candidate != source_id], partner_count)

            for partner_id in partners:
                user1, user2 = sorted((source_id, partner_id))
                key = (user1, user2)
                if key in friendship_keys:
                    continue

                friendship_keys.add(key)
                friends_by_game_user[user1].add(user2)
                friends_by_game_user[user2].add(user1)
                friendships.append((friendship_id, user1, user2))
                friendship_id += 1

    friendly_matches = []
    matches_by_id = {}
    match_id = 1
    start_date = date.today() + timedelta(days=match_start_days)
    for game_id, host_ids in game_users_by_game.items():
        for host_id in host_ids:
            for _ in range(random.randint(0, matches_per_host_max)):
                match_date = start_date + timedelta(days=random.randint(0, match_window_days))
                match_time = f"{random.randint(0, 23):02d}:00:00"
                sort = 0 if random.random() < team_match_rate else 1
                recruit = random.randint(1, 5) if sort == 0 else random.randint(2, 8)
                match = [
                    match_id,
                    host_id,
                    game_id,
                    match_date,
                    match_time,
                    sort,
                    0,
                    recruit,
                    fake.sentence(nb_words=10)[:255],
                ]
                friendly_matches.append(match)
                matches_by_id[match_id] = match
                match_id += 1

    participations = []
    participations_by_match = defaultdict(list)
    participation_keys = set()
    participation_schedules = defaultdict(set)
    participation_id = 1

    def match_schedule_key(match):
        return match[3], match[4]

    def add_participation(game_user_id, current_match_id, role):
        nonlocal participation_id

        key = (game_user_id, current_match_id)
        if key in participation_keys:
            return False

        match = matches_by_id[current_match_id]
        participation_keys.add(key)
        participations.append((participation_id, game_user_id, current_match_id, role))
        participations_by_match[current_match_id].append((game_user_id, role))
        participation_schedules[game_user_id].add(match_schedule_key(match))
        participation_id += 1
        return True

    def has_participation_time_conflict(game_user_id, match):
        return match_schedule_key(match) in participation_schedules[game_user_id]

    solo_matches = [match for match in friendly_matches if match[5] == 1]
    for match in solo_matches:
        if random.random() < solo_host_participation_rate:
            add_participation(match[1], match[0], "host")

    def friends_and_group_members(host_id):
        friends = set(friends_by_game_user[host_id])
        group_id = game_user_by_id[host_id]["group_id"]
        group_members = set(game_users_by_group[group_id]) if group_id is not None else set()
        group_members.discard(host_id)
        return list(friends | group_members)

    team_matches = [match for match in friendly_matches if match[5] == 0]
    for match in team_matches:
        current_match_id = match[0]
        host_id = match[1]
        recruit_limit = match[7]
        candidates = friends_and_group_members(host_id)
        if random.random() < team_host_participation_rate:
            candidates.append(host_id)

        random.shuffle(candidates)
        added = 0
        for candidate_id in candidates:
            if added >= recruit_limit:
                break
            if has_participation_time_conflict(candidate_id, match):
                continue
            if add_participation(candidate_id, current_match_id, "host"):
                added += 1

    requests = []
    requests_by_match = defaultdict(list)
    request_by_id = {}
    request_keys = set()
    request_id = 1
    request_participations = []
    request_participations_by_request = defaultdict(list)
    request_participation_keys = set()
    request_participation_id = 1
    request_schedules = defaultdict(set)

    def has_existing_schedule(game_user_id, match):
        key = match_schedule_key(match)
        return key in participation_schedules[game_user_id] or key in request_schedules[game_user_id]

    def add_request(game_user_id, current_match_id, comment, status):
        nonlocal request_id

        key = (game_user_id, current_match_id)
        if key in request_keys:
            return None

        request = [
            request_id,
            game_user_id,
            current_match_id,
            comment,
            status,
            datetime.now() - timedelta(days=random.randint(0, 10)),
        ]
        request_keys.add(key)
        requests.append(request)
        requests_by_match[current_match_id].append(request)
        request_by_id[request_id] = request
        request_id += 1
        return request

    def add_request_participation(current_request_id, game_user_id):
        nonlocal request_participation_id

        key = (current_request_id, game_user_id)
        if key in request_participation_keys:
            return False

        request = request_by_id[current_request_id]
        match = matches_by_id[request[2]]
        request_participation_keys.add(key)
        request_participations.append((request_participation_id, current_request_id, game_user_id))
        request_participations_by_request[current_request_id].append(game_user_id)
        request_participation_id += 1

        if request[4] != "reject":
            request_schedules[game_user_id].add(match_schedule_key(match))

        return True

    def has_host_participation(current_match_id):
        return any(role == "host" for _, role in participations_by_match[current_match_id])

    for match in solo_matches:
        current_match_id = match[0]
        host_id = match[1]
        game_id = match[2]
        recruit = match[7]
        candidates = [game_user_id for game_user_id in game_users_by_game.get(game_id, []) if game_user_id != host_id]
        if not candidates:
            continue

        adjusted_recruit = max(recruit - (1 if has_host_participation(current_match_id) else 0), 0)
        over_recruit = random.random() >= 0.5
        request_count = (
            random.randint(0, adjusted_recruit)
            if not over_recruit
            else random.randint(adjusted_recruit, adjusted_recruit * 2)
        )
        approve_rate = solo_approve_rate_over_recruit if over_recruit else solo_approve_rate_under_recruit

        approved_count = 0
        random.shuffle(candidates)
        for game_user_id in candidates[:request_count]:
            if has_existing_schedule(game_user_id, match):
                continue

            if approved_count < adjusted_recruit and random.random() < approve_rate:
                status = "approve"
                approved_count += 1
            else:
                status = "await" if random.random() < solo_await_rate else "reject"

            if approved_count >= adjusted_recruit and status == "await":
                status = "reject"

            request = add_request(game_user_id, current_match_id, fake.text(max_nb_chars=30), status)
            if request is not None:
                add_request_participation(request[0], game_user_id)

    for match in team_matches:
        current_match_id = match[0]
        host_id = match[1]
        game_id = match[2]
        candidates = [game_user_id for game_user_id in game_users_by_game.get(game_id, []) if game_user_id != host_id]
        if not candidates:
            continue

        random.shuffle(candidates)
        request_count = min(random.randint(0, requests_per_team_match_max), len(candidates))
        has_approved = False

        for game_user_id in candidates[:request_count]:
            if has_existing_schedule(game_user_id, match):
                continue

            if not has_approved and random.random() < team_approve_rate:
                status = "approve"
                has_approved = True
            else:
                status = "reject" if has_approved else "await"

            add_request(game_user_id, current_match_id, fake.text(max_nb_chars=30), status)

    for match in team_matches:
        current_match_id = match[0]
        host_id = match[1]
        game_id = match[2]
        recruit = match[7]
        candidates = [game_user_id for game_user_id in game_users_by_game.get(game_id, []) if game_user_id != host_id]

        for request in requests_by_match[current_match_id]:
            current_request_id = request[0]
            requester_id = request[1]
            participants = []

            if not has_existing_schedule(requester_id, match):
                participants.append(requester_id)

            random.shuffle(candidates)
            for game_user_id in candidates:
                if len(participants) >= recruit:
                    break
                if game_user_id in participants or has_existing_schedule(game_user_id, match):
                    continue
                participants.append(game_user_id)

            for participant_id in participants:
                add_request_participation(current_request_id, participant_id)

    for request in requests:
        if request[4] != "approve":
            continue

        current_request_id = request[0]
        current_match_id = request[2]
        for game_user_id in request_participations_by_request[current_request_id]:
            add_participation(game_user_id, current_match_id, "client")

    for match in friendly_matches:
        current_match_id = match[0]
        sort = match[5]
        recruit = match[7]
        match_participations = participations_by_match[current_match_id]

        if sort == 0 and any(role == "client" for _, role in match_participations):
            match[6] = 1
        elif sort == 1 and len(match_participations) >= recruit:
            match[6] = 1

    return {
        "game": games,
        "user": users,
        "group": groups,
        "game_user": game_users,
        "post": posts,
        "comment": comments,
        "post_recommendation_user": recommendations,
        "friendship": friendships,
        "friendly_match": [tuple(match) for match in friendly_matches],
        "friendly_match_participation": participations,
        "friendly_match_request": [tuple(request) for request in requests],
        "friendly_match_request_participation": request_participations,
    }


def build_seed_data_smoke_unused():
    random.seed(env_int("SEED_RANDOM_SEED", 20260511))
    fake.seed_instance(env_int("SEED_RANDOM_SEED", 20260511))

    user_count = env_int("SEED_USER_COUNT", 100)
    groups_per_game = env_int("SEED_GROUPS_PER_GAME", 2)
    max_games_per_user = env_int("SEED_MAX_GAMES_PER_USER", 3)
    posts_per_game = env_int("SEED_POSTS_PER_GAME", 3)
    max_root_comments = env_int("SEED_MAX_ROOT_COMMENTS_PER_POST", 2)
    max_replies = env_int("SEED_MAX_REPLIES_PER_COMMENT", 2)
    friends_per_game_user = env_int("SEED_FRIENDS_PER_GAME_USER", 3)
    matches_per_host_max = env_int("SEED_MATCHES_PER_HOST_MAX", 1)
    match_window_days = env_int("SEED_MATCH_WINDOW_DAYS", 10)
    team_match_rate = env_float("SEED_TEAM_MATCH_RATE", 0.65)
    password_hash = os.getenv("SEED_USER_PASSWORD_HASH", DEFAULT_PASSWORD_HASH)

    games = [(index, name, sort, url) for index, (name, sort, url) in enumerate(GAMES, start=1)]

    login_ids = set()
    emails = set()
    phones = set()
    users = []
    for user_id in range(1, user_count + 1):
        while True:
            email = f"{fake.unique.user_name()}@{random.choice(EMAIL_DOMAINS)}"
            if email not in emails:
                emails.add(email)
                break
        users.append((
            user_id,
            fake.name(),
            email,
            "",
            unique_phone(phones),
            random_birth(),
            unique_login_id(login_ids),
            password_hash,
        ))

    groups = []
    group_id = 1
    groups_by_game = defaultdict(list)
    for game_id, *_ in games:
        for _ in range(groups_per_game):
            name = random.choice([f"{korean_word(2)} {fake.word()}", fake.word()])[:255]
            groups.append((group_id, game_id, name))
            groups_by_game[game_id].append(group_id)
            group_id += 1

    game_users = []
    game_users_by_game = defaultdict(list)
    game_users_by_user = defaultdict(list)
    nickname_keys = set()
    game_user_id = 1
    game_ids = [game[0] for game in games]
    for user_id, *_ in users:
        sample_size = random.randint(1, min(max_games_per_user, len(game_ids)))
        for game_id in random.sample(game_ids, sample_size):
            for _ in range(20):
                nickname = f"{korean_word(2)}{fake.word()}{random.randint(1, 999)}"[:255]
                key = (nickname, game_id)
                if key not in nickname_keys:
                    nickname_keys.add(key)
                    break
            else:
                continue
            group_id = random.choice(groups_by_game[game_id]) if random.random() < 0.7 else None
            game_users.append((game_user_id, user_id, game_id, group_id, nickname))
            game_users_by_game[game_id].append(game_user_id)
            game_users_by_user[user_id].append(game_user_id)
            game_user_id += 1

    posts = []
    post_id = 1
    user_ids = [user[0] for user in users]
    for game_id, *_ in games:
        for _ in range(posts_per_game):
            posts.append((
                post_id,
                fake.sentence(nb_words=4)[:15],
                random_seed_date(-120, 0),
                random.randint(0, 500),
                random.randint(0, 120),
                random.randint(0, 30),
                fake.text(max_nb_chars=45),
                random.choice(user_ids),
                game_id,
                fake.time_object().replace(microsecond=0),
                random.choice([True, False]),
            ))
            post_id += 1

    comments = []
    comment_id = 1
    for post in posts:
        current_post_id = post[0]
        base_date = post[2]
        root_count = random.randint(0, max_root_comments)
        root_ids = []
        for _ in range(root_count):
            comments.append((
                comment_id,
                random.choice(user_ids),
                fake.text(max_nb_chars=45),
                base_date,
                fake.time_object().replace(microsecond=0),
                current_post_id,
                None,
                random.choice([True, False]),
            ))
            root_ids.append(comment_id)
            comment_id += 1
        for root_id in root_ids:
            for _ in range(random.randint(0, max_replies)):
                comments.append((
                    comment_id,
                    random.choice(user_ids),
                    fake.text(max_nb_chars=45),
                    base_date,
                    fake.time_object().replace(microsecond=0),
                    current_post_id,
                    root_id,
                    random.choice([True, False]),
                ))
                comment_id += 1

    recommendations = []
    recommendation_keys = set()
    for post in posts:
        for user_id in random.sample(user_ids, min(random.randint(0, 5), len(user_ids))):
            key = (post[0], user_id)
            if key not in recommendation_keys:
                recommendation_keys.add(key)
                recommendations.append((post[0], user_id, random.choice([True, False])))

    friendships = []
    friendship_keys = set()
    friendship_id = 1
    for game_user_ids in game_users_by_game.values():
        if len(game_user_ids) < 2:
            continue
        for source_id in game_user_ids:
            partner_count = min(friends_per_game_user, len(game_user_ids) - 1)
            for partner_id in random.sample([candidate for candidate in game_user_ids if candidate != source_id], partner_count):
                user1, user2 = sorted((source_id, partner_id))
                key = (user1, user2)
                if key in friendship_keys:
                    continue
                friendship_keys.add(key)
                friendships.append((friendship_id, user1, user2))
                friendship_id += 1

    friendly_matches = []
    match_id = 1
    start_date = date.today() - timedelta(days=2)
    for game_id, host_ids in game_users_by_game.items():
        for host_id in host_ids:
            for _ in range(random.randint(0, matches_per_host_max)):
                sort = 0 if random.random() < team_match_rate else 1
                recruit = random.randint(2, 5) if sort == 0 else random.randint(2, 8)
                match_date = start_date + timedelta(days=random.randint(0, match_window_days))
                match_time = f"{random.randint(0, 23):02d}:00:00"
                friendly_matches.append((
                    match_id,
                    host_id,
                    game_id,
                    match_date,
                    match_time,
                    sort,
                    0,
                    recruit,
                    fake.sentence(nb_words=8)[:255],
                ))
                match_id += 1

    participations = []
    participation_keys = set()
    participation_id = 1
    requests = []
    request_keys = set()
    request_id = 1
    request_participations = []
    request_participation_keys = set()
    request_participation_id = 1
    schedules_by_game_user = defaultdict(set)

    for match in friendly_matches:
        current_match_id, host_id, game_id, match_date, match_time, sort, _, recruit, _ = match
        schedule_key = (match_date, match_time)

        if schedule_key not in schedules_by_game_user[host_id]:
            participations.append((participation_id, host_id, current_match_id, "host"))
            participation_keys.add((host_id, current_match_id))
            schedules_by_game_user[host_id].add(schedule_key)
            participation_id += 1

        candidates = [candidate for candidate in game_users_by_game[game_id] if candidate != host_id]
        random.shuffle(candidates)
        request_count = min(random.randint(0, 3), len(candidates))
        approved_exists = False

        for requester_id in candidates[:request_count]:
            key = (requester_id, current_match_id)
            if key in request_keys:
                continue
            if schedule_key in schedules_by_game_user[requester_id]:
                continue

            status = "approve" if not approved_exists and random.random() < 0.25 else random.choice(["await", "reject"])
            approved_exists = approved_exists or status == "approve"
            requests.append((
                request_id,
                requester_id,
                current_match_id,
                fake.text(max_nb_chars=40),
                status,
                datetime.now() - timedelta(days=random.randint(0, 10)),
            ))
            request_keys.add(key)

            members = [requester_id]
            if sort == 0:
                extra_candidates = [candidate for candidate in game_users_by_game[game_id] if candidate not in (host_id, requester_id)]
                random.shuffle(extra_candidates)
                members.extend(extra_candidates[:max(0, min(recruit - 1, random.randint(0, 3)))])

            for member_id in dict.fromkeys(members):
                rp_key = (request_id, member_id)
                if rp_key not in request_participation_keys:
                    request_participations.append((request_participation_id, request_id, member_id))
                    request_participation_keys.add(rp_key)
                    request_participation_id += 1
                if status == "approve" and (member_id, current_match_id) not in participation_keys:
                    participations.append((participation_id, member_id, current_match_id, "client"))
                    participation_keys.add((member_id, current_match_id))
                    schedules_by_game_user[member_id].add(schedule_key)
                    participation_id += 1

            request_id += 1

        approved_count = sum(1 for request in requests if request[2] == current_match_id and request[4] == "approve")
        if match_date < date.today() or approved_count > 0:
            friendly_matches[current_match_id - 1] = (*match[:6], 1, *match[7:])

    return {
        "game": games,
        "user": users,
        "group": groups,
        "game_user": game_users,
        "post": posts,
        "comment": comments,
        "post_recommendation_user": recommendations,
        "friendship": friendships,
        "friendly_match": friendly_matches,
        "friendly_match_participation": participations,
        "friendly_match_request": requests,
        "friendly_match_request_participation": request_participations,
    }


def build_sql(data):
    tables_for_truncate = [
        "friendly_match_request_participation",
        "friendly_match_request",
        "friendly_match_participation",
        "friendly_match",
        "comment",
        "post_recommendation_user",
        "image_post",
        "post",
        "friendship",
        "refresh_token",
        "game_user",
        "group",
        "game",
        "user",
    ]

    statements = [
        "truncate table "
        + ", ".join(q_identifier(table) for table in tables_for_truncate)
        + " restart identity cascade;"
    ]

    statements.extend(insert_rows("game", ["id", "name", "sort", "url"], data["game"]))
    statements.extend(insert_rows("user", ["id", "name", "email", "profile", "phone_number", "birth", "login_id", "login_password"], data["user"]))
    statements.extend(insert_rows("group", ["id", "game_id", "name"], data["group"]))
    statements.extend(insert_rows("game_user", ["id", "user_id", "game_id", "group_id", "nickname"], data["game_user"]))
    statements.extend(insert_rows("post", ["id", "title", "date", "views", "recommendations", "dislikes", "content", "user_id", "game_id", "time", "anonymous"], data["post"]))
    statements.extend(insert_rows("comment", ["id", "user_id", "content", "date", "time", "post_id", "comment_id", "anonymous"], data["comment"]))
    recommendation_rows = [
        (post_id, user_id, post_id, user_id, good_or_bad)
        for post_id, user_id, good_or_bad in data["post_recommendation_user"]
    ]
    statements.extend(insert_rows(
        "post_recommendation_user",
        ["post_Id", "user_Id", "post_id", "user_id", "goodorbad"],
        recommendation_rows,
    ))
    statements.extend(insert_rows("friendship", ["id", "game_user_id_1", "game_user_id_2"], data["friendship"]))
    statements.extend(insert_rows("friendly_match", ["id", "host_id", "game_id", "date", "time", "sort", "state", "recruit", "comment"], data["friendly_match"]))
    statements.extend(insert_rows("friendly_match_participation", ["id", "game_user_id", "friendly_match_id", "role"], data["friendly_match_participation"]))
    statements.extend(insert_rows("friendly_match_request", ["id", "game_user_id", "friendly_match_id", "comment", "status", "updated_at"], data["friendly_match_request"]))
    statements.extend(insert_rows("friendly_match_request_participation", ["id", "friendly_match_request_id", "game_user_id"], data["friendly_match_request_participation"]))
    statements.extend(reset_sequences([
        "game",
        "user",
        "group",
        "game_user",
        "post",
        "comment",
        "friendship",
        "friendly_match",
        "friendly_match_participation",
        "friendly_match_request",
        "friendly_match_request_participation",
    ]))

    return "\n".join(statements) + "\n"


def find_postgres_jar():
    candidates = sorted(Path.home().glob(".gradle/caches/modules-2/files-2.1/org.postgresql/postgresql/*/*/postgresql-*.jar"), reverse=True)
    for candidate in candidates:
        if candidate.exists() and not candidate.name.endswith("-sources.jar"):
            return candidate
    raise RuntimeError("PostgreSQL JDBC jar를 찾지 못했습니다. 먼저 ./gradlew compileJava를 실행해주세요.")


def run_sql(sql):
    postgres_jar = find_postgres_jar()
    with tempfile.TemporaryDirectory(prefix="game-match-supabase-seed-") as tmp:
        tmp_path = Path(tmp)
        sql_file = tmp_path / "seed.sql"
        class_dir = tmp_path / "classes"
        class_dir.mkdir()
        sql_file.write_text(sql, encoding="utf-8")

        subprocess.run(["javac", "-cp", str(postgres_jar), "-d", str(class_dir), str(RUNNER_SOURCE)], check=True)
        subprocess.run(["java", "-cp", f"{class_dir}:{postgres_jar}", "JdbcSqlRunner", str(sql_file)], check=True)


def main():
    load_env_file(PROJECT_ROOT / ".env")
    required = ["SUPABASE_DB_URL", "SUPABASE_DB_USERNAME", "SUPABASE_DB_PASSWORD"]
    missing = [name for name in required if not os.getenv(name)]
    if missing:
        raise SystemExit("Missing env: " + ", ".join(missing))

    data = build_seed_data()
    print("[seed] generated", flush=True)
    for table, rows in data.items():
        print(f"[seed] {table}={len(rows)}", flush=True)

    run_sql(build_sql(data))
    print("[done] Supabase seed completed", flush=True)


if __name__ == "__main__":
    main()
