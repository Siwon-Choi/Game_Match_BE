import argparse
import os
import random
import string
import sys
from contextlib import contextmanager
from datetime import date, datetime, timedelta
from pathlib import Path

import mysql.connector
from faker import Faker

PACKAGE_DIR = Path(__file__).resolve().parent
MAIN_DIR = PACKAGE_DIR.parent
PROJECT_ROOT = MAIN_DIR.parent
DEFAULT_SCHEMA_PATH = MAIN_DIR / "game_match.sql"
DEFAULT_SEED_PASSWORD_HASH = "$2a$10$cnJb/JM1/yh9Y.jGvZKi6ujHfJBoMOlHaX0nvR6r1zLKSaVPR4Oey"

fake = Faker("ko_KR")

EMAIL_DOMAINS = ["naver.com", "gmail.com", "daum.net", "hanmail.net", "nate.com"]
SPECIAL_CHARS = "!@#$%^&*()"
CHAT_GAMES = [
    "롤",
    "발로란트",
    "FC 온라인",
    "메이플",
    "던파",
    "서든",
    "카트",
    "파판14",
]
CHAT_TIMES = ["지금", "10분 뒤", "저녁에", "주말에", "한 판만", "두 판 정도"]
CHAT_ROLES = ["탱커", "딜러", "힐러", "골키퍼", "공격수", "서포터"]
CHAT_SCRIPTS = [
    [
        "{game} 가능하세요?",
        "네 가능해요. {time} 할까요?",
        "좋아요. 포지션 뭐 하세요?",
        "저 {role} 가능해요.",
        "그럼 제가 남는 포지션 볼게요.",
        "디코도 가능하세요?",
        "네 가능해요. 방 만들면 알려주세요.",
        "알겠습니다. 초대 보낼게요.",
    ],
    [
        "오늘 {game} 같이 하실래요?",
        "좋아요. 몇 시쯤 생각하세요?",
        "{time} 괜찮을 것 같아요.",
        "그때 접속해 있을게요.",
        "실력 상관없이 편하게 해요.",
        "네 저도 즐겜 위주라 괜찮아요.",
    ],
    [
        "혹시 자리 남았나요?",
        "네 한 자리 남았어요.",
        "저 들어가도 될까요?",
        "가능합니다. 닉네임 알려주세요.",
        "보내드렸어요.",
        "확인했습니다. 초대할게요.",
    ],
    [
        "매칭 아직 구하시나요?",
        "네 아직 구하는 중이에요.",
        "저 {role} 가능한데 괜찮나요?",
        "좋습니다. 바로 시작 가능하세요?",
        "{time} 가능해요.",
        "그럼 시간 맞춰서 들어와 주세요.",
    ],
    [
        "방금 신청 넣었습니다.",
        "확인했어요. 승인해드릴게요.",
        "감사합니다.",
        "마이크 가능하시면 더 편할 것 같아요.",
        "가능합니다.",
        "좋아요. 이따 봐요.",
    ],
]
CHAT_FALLBACKS = [
    "넵 확인했습니다.",
    "잠깐만요.",
    "좋아요.",
    "괜찮습니다.",
    "저도 가능합니다.",
    "초대 보내주세요.",
    "끝나고 한 판 더 가능해요.",
    "오늘은 가볍게 해요.",
    "ㅋㅋ 좋아요.",
    "시간 맞춰서 접속할게요.",
]

TABLE_GROUPS = {
    "content": ["`Comment`", "`Post_Recommendation_User`", "`image_post`", "`Post`"],
    "friendly": [
        "`friendly_match_request_participation`",
        "`friendly_match_request`",
        "`friendly_match_participation`",
        "`friendly_match`",
    ],
    "social": ["`Friendship`"],
    "accounts": ["`Refresh_Token`", "`game_user`", "`Group`", "`User`"],
}
ALL_SEED_TABLES = [table for tables in TABLE_GROUPS.values() for table in tables]


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


def mysql_config(include_database=True):
    config = {
        "user": os.getenv("DB_USERNAME", "root"),
        "password": os.getenv("DB_PASSWORD", ""),
        "host": os.getenv("DB_HOST", "localhost"),
        "port": int(os.getenv("DB_PORT", "3306")),
        "charset": os.getenv("DB_CHARSET", "utf8mb4"),
    }
    if include_database:
        config["database"] = os.getenv("DB_NAME", "game_match")
    return config


def connect(include_database=True):
    return mysql.connector.connect(**mysql_config(include_database=include_database))


@contextmanager
def db_cursor(buffered=False):
    connection = connect()
    cursor = connection.cursor(buffered=buffered)
    try:
        yield connection, cursor
        connection.commit()
    except Exception:
        connection.rollback()
        raise
    finally:
        cursor.close()
        connection.close()


def fetch_all(cursor, query, params=None):
    cursor.execute(query, params or ())
    return cursor.fetchall()


def grouped_values(rows):
    grouped = {}
    for key, value in rows:
        grouped.setdefault(key, []).append(value)
    return grouped


def korean_word(length=2):
    return "".join(chr(random.randint(0xAC00, 0xD7A3)) for _ in range(length))


def random_date(start_days=-365, end_days=0):
    start = date.today() + timedelta(days=start_days)
    end = date.today() + timedelta(days=end_days)
    return fake.date_between(start_date=start, end_date=end)


def random_chat_context():
    return {
        "game": random.choice(CHAT_GAMES),
        "time": random.choice(CHAT_TIMES),
        "role": random.choice(CHAT_ROLES),
    }


def chat_message(script, index, context):
    if index < len(script):
        return script[index].format(**context)
    return random.choice(CHAT_FALLBACKS).format(**context)


def split_sql_statements(sql):
    statements = []
    current = []
    in_single_quote = False
    in_double_quote = False
    escaped = False

    for char in sql:
        current.append(char)

        if char == "\\" and not escaped:
            escaped = True
            continue

        if char == "'" and not escaped and not in_double_quote:
            in_single_quote = not in_single_quote
        elif char == '"' and not escaped and not in_single_quote:
            in_double_quote = not in_double_quote
        elif char == ";" and not in_single_quote and not in_double_quote:
            statement = "".join(current).strip()
            if statement:
                statements.append(statement[:-1].strip())
            current = []

        escaped = False

    tail = "".join(current).strip()
    if tail:
        statements.append(tail)
    return statements


def execute_schema(schema_path):
    sql = schema_path.read_text(encoding="utf-8")
    db_name = os.getenv("DB_NAME", "game_match").replace("`", "``")
    sql = sql.replace("__DB_NAME__", f"`{db_name}`")
    statements = split_sql_statements(sql)

    connection = connect(include_database=False)
    connection.autocommit = True
    cursor = connection.cursor()
    try:
        for statement in statements:
            if statement:
                cursor.execute(statement)
    finally:
        cursor.close()
        connection.close()


def reset_seed_tables(tables=ALL_SEED_TABLES):
    with db_cursor() as (_, cursor):
        cursor.execute("SET FOREIGN_KEY_CHECKS=0")
        for table in tables:
            try:
                cursor.execute(f"TRUNCATE TABLE {table}")
            except mysql.connector.Error:
                cursor.execute(f"DELETE FROM {table}")
                try:
                    cursor.execute(f"ALTER TABLE {table} AUTO_INCREMENT = 1")
                except mysql.connector.Error:
                    pass
        cursor.execute("SET FOREIGN_KEY_CHECKS=1")


def generate_phone(existing):
    while True:
        phone = f"010-{random.randint(0, 9999):04d}-{random.randint(0, 9999):04d}"
        if phone not in existing:
            existing.add(phone)
            return phone


def generate_login_id(existing):
    while True:
        login_id = "".join(random.choices(string.ascii_lowercase, k=random.randint(3, 7)))
        login_id += "".join(random.choices(string.digits, k=random.randint(2, 3)))
        if login_id not in existing:
            existing.add(login_id)
            return login_id


def generate_password():
    length = random.randint(8, 12)
    normal = string.ascii_lowercase + string.digits
    password = random.choices(normal, k=length - 2)
    password += random.choices(SPECIAL_CHARS, k=2)
    random.shuffle(password)
    return "".join(password)


def seed_password_hash():
    return os.getenv("SEED_USER_PASSWORD_HASH", DEFAULT_SEED_PASSWORD_HASH)


def generate_birth():
    year = min(2010, max(1980, int(random.gauss(1995, 6))))
    return date(year, random.randint(1, 12), random.randint(1, 28))


def seed_users():
    user_count = env_int("SEED_USER_COUNT", 10_000)
    fake.unique.clear()

    with db_cursor() as (_, cursor):
        cursor.execute("SELECT Login_Id, Email, Phone_Number FROM `User`")
        existing_login_ids = set()
        existing_emails = set()
        existing_phones = set()
        for login_id, email, phone in cursor.fetchall():
            existing_login_ids.add(login_id)
            existing_emails.add(email)
            existing_phones.add(phone)

        rows = []
        for _ in range(user_count):
            while True:
                email = f"{fake.unique.user_name()}@{random.choice(EMAIL_DOMAINS)}"
                if email not in existing_emails:
                    existing_emails.add(email)
                    break

            rows.append((
                fake.name(),
                email,
                None,
                generate_phone(existing_phones),
                generate_birth(),
                generate_login_id(existing_login_ids),
                seed_password_hash(),
            ))

        if rows:
            cursor.executemany(
                """
                INSERT INTO `User` (Name, Email, Profile, Phone_Number, Birth, Login_Id, Login_Password)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                """,
                rows,
            )


def seed_groups():
    groups_per_game = env_int("SEED_GROUPS_PER_GAME", 10)

    with db_cursor() as (_, cursor):
        games = fetch_all(cursor, "SELECT Id FROM `Game`")
        rows = []
        for (game_id,) in games:
            for _ in range(groups_per_game):
                group_name = random.choice([f"{korean_word(2)} {fake.word()}", fake.word()])
                rows.append((game_id, group_name[:255]))

        if rows:
            cursor.executemany(
                "INSERT INTO `Group` (Game_Id, Name) VALUES (%s, %s)",
                rows,
            )


def seed_game_users():
    max_games_per_user = env_int("SEED_MAX_GAMES_PER_USER", 5)
    group_join_rate = env_float("SEED_GROUP_JOIN_RATE", 0.7)

    with db_cursor() as (_, cursor):
        users = fetch_all(cursor, "SELECT Id FROM `User`")
        games = fetch_all(cursor, "SELECT Id FROM `Game`")
        groups_by_game = grouped_values(fetch_all(cursor, "SELECT Game_Id, Id FROM `Group`"))
        existing_nicknames = set(fetch_all(cursor, "SELECT Nickname, Game_Id FROM `game_user`"))

        if not users or not games:
            return

        for (user_id,) in users:
            count = random.randint(0, min(max_games_per_user, len(games)))
            for (game_id,) in random.sample(games, count):
                for _ in range(10):
                    nickname = f"{korean_word(3)}{fake.word()}"[:255]
                    nickname_key = (nickname, game_id)
                    if nickname_key not in existing_nicknames:
                        existing_nicknames.add(nickname_key)
                        break
                else:
                    continue

                eligible_groups = groups_by_game.get(game_id, [])
                group_id = random.choice(eligible_groups) if eligible_groups and random.random() < group_join_rate else None

                try:
                    cursor.execute(
                        """
                        INSERT INTO `game_user` (User_Id, Game_Id, Nickname, Group_Id)
                        VALUES (%s, %s, %s, %s)
                        """,
                        (user_id, game_id, nickname, group_id),
                    )
                except mysql.connector.IntegrityError:
                    continue


def seed_posts():
    posts_per_game = env_int("SEED_POSTS_PER_GAME", 100)

    with db_cursor() as (_, cursor):
        games = fetch_all(cursor, "SELECT Id FROM `Game`")
        users = fetch_all(cursor, "SELECT Id FROM `User`")
        if not games or not users:
            return

        rows = []
        for (game_id,) in games:
            for _ in range(posts_per_game):
                rows.append((
                    fake.sentence(nb_words=4)[:15],
                    random_date(-365, 0),
                    random.randint(0, 1_000),
                    random.randint(0, 500),
                    fake.text(max_nb_chars=45),
                    random.choice(users)[0],
                    fake.time_object(),
                    game_id,
                    random.randint(0, 100),
                    random.choice([True, False]),
                ))

        if rows:
            cursor.executemany(
                """
                INSERT INTO `Post` (title, date, views, recommendations, content, user_id, time, game_id, dislikes, anonymous)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                """,
                rows,
            )


def normalize_time(value):
    if hasattr(value, "seconds"):
        return (datetime.min + value).time()
    return value


def insert_comment(cursor, user_id, content, comment_date, comment_time, post_id, parent_id=None):
    cursor.execute(
        """
        INSERT INTO `Comment` (user_Id, content, date, time, post_Id, comment_Id, anonymous)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
        """,
        (user_id, content, comment_date, comment_time, post_id, parent_id, random.choice([True, False])),
    )
    return cursor.lastrowid


def seed_comments():
    max_root_comments_per_post = env_int("SEED_MAX_ROOT_COMMENTS_PER_POST", 10)
    max_replies_per_comment = env_int("SEED_MAX_REPLIES_PER_COMMENT", 5)

    with db_cursor() as (_, cursor):
        posts = fetch_all(cursor, "SELECT Id, date, time FROM `Post`")
        users = fetch_all(cursor, "SELECT Id FROM `User`")
        if not posts or not users:
            return

        for post_id, post_date, post_time in posts:
            post_time = normalize_time(post_time)
            root_count = random.randint(1, max_root_comments_per_post)

            for _ in range(root_count):
                base_dt = datetime.combine(post_date, post_time) + timedelta(minutes=random.randint(1, 1440))
                root_id = insert_comment(
                    cursor,
                    random.choice(users)[0],
                    fake.text(max_nb_chars=45),
                    base_dt.date(),
                    base_dt.time(),
                    post_id,
                )

                reply_count = random.randint(0, max_replies_per_comment)
                for _ in range(reply_count):
                    reply_dt = base_dt + timedelta(minutes=random.randint(1, 1440))
                    insert_comment(
                        cursor,
                        random.choice(users)[0],
                        fake.text(max_nb_chars=45),
                        reply_dt.date(),
                        reply_dt.time(),
                        post_id,
                        root_id,
                    )


def create_room(cursor, receiver_id, sender_id, last_message_time):
    cursor.execute(
        """
        INSERT INTO `Chatting_Room` (Receiver_Id, Sender_Id, Last_Chat_Time)
        VALUES (%s, %s, %s)
        ON DUPLICATE KEY UPDATE Last_Chat_Time = VALUES(Last_Chat_Time)
        """,
        (receiver_id, sender_id, last_message_time),
    )


def seed_chat():
    chat_user_limit = env_int("SEED_CHAT_USER_LIMIT", 10_000)
    chat_partners_per_user = env_int("SEED_CHAT_PARTNERS_PER_USER", 10)
    messages_per_side_min = env_int("SEED_CHAT_MESSAGES_PER_SIDE_MIN", 5)
    messages_per_side_max = env_int("SEED_CHAT_MESSAGES_PER_SIDE_MAX", 10)
    insert_batch_size = env_int("SEED_CHAT_INSERT_BATCH_SIZE", 5_000)

    with db_cursor() as (_, cursor):
        users = [row[0] for row in fetch_all(cursor, "SELECT Id FROM `User` ORDER BY Id LIMIT %s", (chat_user_limit,))]
        if len(users) < 2:
            return

        chat_rows = []
        room_rows = []
        created_pairs = set()

        def flush_rows():
            if chat_rows:
                cursor.executemany(
                    """
                    INSERT INTO `Chat` (Receiver_Id, Sender_Id, Timestamped, Message)
                    VALUES (%s, %s, %s, %s)
                    """,
                    chat_rows,
                )
                chat_rows.clear()

            if room_rows:
                cursor.executemany(
                    """
                    INSERT INTO `Chatting_Room` (Receiver_Id, Sender_Id, Last_Chat_Time)
                    VALUES (%s, %s, %s)
                    ON DUPLICATE KEY UPDATE Last_Chat_Time = VALUES(Last_Chat_Time)
                    """,
                    room_rows,
                )
                room_rows.clear()

        for user_id in users:
            partner_count = min(chat_partners_per_user, len(users) - 1)
            chosen_count = 0
            attempts = 0
            max_attempts = partner_count * 30

            while chosen_count < partner_count and attempts < max_attempts:
                partner_id = random.choice(users)
                attempts += 1
                if partner_id == user_id:
                    continue
                pair = tuple(sorted((user_id, partner_id)))
                if pair in created_pairs:
                    continue
                created_pairs.add(pair)
                chosen_count += 1

                last_message_time = datetime.now() - timedelta(days=random.randint(0, 30))
                messages_per_side = random.randint(messages_per_side_min, messages_per_side_max)
                script = random.choice(CHAT_SCRIPTS)
                context = random_chat_context()

                for index in range(messages_per_side * 2):
                    sender_id, receiver_id = (user_id, partner_id) if index % 2 == 0 else (partner_id, user_id)
                    last_message_time += timedelta(minutes=random.randint(1, 5))
                    chat_rows.append((receiver_id, sender_id, last_message_time, chat_message(script, index, context)))

                room_rows.append((user_id, partner_id, last_message_time))
                room_rows.append((partner_id, user_id, last_message_time))

                if len(chat_rows) >= insert_batch_size or len(room_rows) >= insert_batch_size:
                    flush_rows()

        flush_rows()


def seed_friendships():
    friends_per_game_user = env_int("SEED_FRIENDS_PER_GAME_USER", 10)

    with db_cursor() as (_, cursor):
        users_by_game = grouped_values(fetch_all(cursor, "SELECT Game_Id, Id FROM `game_user`"))

        for game_user_ids in users_by_game.values():
            if len(game_user_ids) < 2:
                continue

            for game_user_id in game_user_ids:
                partner_count = min(friends_per_game_user, len(game_user_ids) - 1)
                partners = random.sample([candidate for candidate in game_user_ids if candidate != game_user_id], partner_count)

                for partner_id in partners:
                    user1, user2 = sorted((game_user_id, partner_id))
                    try:
                        cursor.execute(
                            "INSERT INTO `Friendship` (Game_User_Id_1, Game_User_Id_2) VALUES (%s, %s)",
                            (user1, user2),
                        )
                    except mysql.connector.IntegrityError:
                        continue


def seed_friendly_matches():
    matches_per_host_max = env_int("SEED_MATCHES_PER_HOST_MAX", 3)
    match_start_days = env_int("SEED_MATCH_START_DAYS", -3)
    match_window_days = env_int("SEED_MATCH_WINDOW_DAYS", 6)
    team_match_rate = env_float("SEED_TEAM_MATCH_RATE", 0.8)
    start_date = date.today() + timedelta(days=match_start_days)

    with db_cursor() as (_, cursor):
        users_by_game = grouped_values(fetch_all(cursor, "SELECT Game_Id, Id FROM `game_user`"))

        for game_id, host_ids in users_by_game.items():
            for host_id in host_ids:
                for _ in range(random.randint(0, matches_per_host_max)):
                    match_date = start_date + timedelta(days=random.randint(0, match_window_days))
                    match_time = f"{random.randint(0, 23):02d}:00:00"
                    sort = 0 if random.random() < team_match_rate else 1
                    recruit = random.randint(1, 5) if sort == 0 else random.randint(2, 8)

                    try:
                        cursor.execute(
                            """
                            INSERT INTO `friendly_match` (Host_Id, Game_Id, Date, Time, Sort, Comment, State, Recruit)
                            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                            """,
                            (host_id, game_id, match_date, match_time, sort, fake.sentence(nb_words=10), 0, recruit),
                        )
                    except mysql.connector.IntegrityError:
                        continue


def seed_friendly_match_participation_solo():
    host_participation_rate = env_float("SEED_SOLO_HOST_PARTICIPATION_RATE", 0.8)

    with db_cursor() as (_, cursor):
        matches = fetch_all(cursor, "SELECT Id, Host_Id FROM `friendly_match` WHERE Sort = 1")
        rows = [
            (host_id, match_id)
            for match_id, host_id in matches
            if random.random() < host_participation_rate
        ]
        if rows:
            cursor.executemany(
                """
                INSERT IGNORE INTO `friendly_match_participation` (Game_User_Id, Friendly_Match_Id, Role)
                VALUES (%s, %s, 'host')
                """,
                rows,
            )


def friends_and_group_members(cursor, host_id):
    cursor.execute(
        """
        SELECT Game_User_Id_2 FROM `Friendship` WHERE Game_User_Id_1 = %s
        UNION
        SELECT Game_User_Id_1 FROM `Friendship` WHERE Game_User_Id_2 = %s
        """,
        (host_id, host_id),
    )
    friends = {row[0] for row in cursor.fetchall()}

    cursor.execute(
        """
        SELECT gu.Id
        FROM `game_user` gu
        WHERE gu.Group_Id = (SELECT Group_Id FROM `game_user` WHERE Id = %s)
        AND gu.Id != %s
        """,
        (host_id, host_id),
    )
    group_members = {row[0] for row in cursor.fetchall()}
    return list(friends | group_members)


def has_participation_time_conflict(cursor, game_user_id, match_date, match_time):
    cursor.execute(
        """
        SELECT 1
        FROM `friendly_match_participation` p
        JOIN `friendly_match` fm ON p.Friendly_Match_Id = fm.Id
        WHERE p.Game_User_Id = %s AND fm.Date = %s AND fm.Time = %s
        LIMIT 1
        """,
        (game_user_id, match_date, match_time),
    )
    return cursor.fetchone() is not None


def seed_friendly_match_participation_team():
    host_participation_rate = env_float("SEED_TEAM_HOST_PARTICIPATION_RATE", 0.8)

    with db_cursor() as (_, cursor):
        matches = fetch_all(cursor, "SELECT Id, Host_Id, Date, Time, Recruit FROM `friendly_match` WHERE Sort = 0")

        for match_id, host_id, match_date, match_time, recruit_limit in matches:
            candidates = friends_and_group_members(cursor, host_id)
            if random.random() < host_participation_rate:
                candidates.append(host_id)

            random.shuffle(candidates)
            added = 0
            for candidate_id in candidates:
                if added >= recruit_limit:
                    break
                if has_participation_time_conflict(cursor, candidate_id, match_date, match_time):
                    continue

                cursor.execute(
                    """
                    INSERT IGNORE INTO `friendly_match_participation` (Game_User_Id, Friendly_Match_Id, Role)
                    VALUES (%s, %s, 'host')
                    """,
                    (candidate_id, match_id),
                )
                added += cursor.rowcount


def has_existing_schedule(cursor, game_user_id, match_date, match_time):
    cursor.execute(
        """
        SELECT 1
        FROM `friendly_match_participation` p
        JOIN `friendly_match` m ON p.Friendly_Match_Id = m.Id
        WHERE p.Game_User_Id = %s AND m.Date = %s AND m.Time = %s
        UNION
        SELECT 1
        FROM `friendly_match_request_participation` rp
        JOIN `friendly_match_request` r ON rp.Friendly_match_request_id = r.Id
        JOIN `friendly_match` m ON r.Friendly_Match_Id = m.Id
        WHERE rp.Game_user_id = %s AND m.Date = %s AND m.Time = %s AND r.status <> 'reject'
        LIMIT 1
        """,
        (game_user_id, match_date, match_time, game_user_id, match_date, match_time),
    )
    return cursor.fetchone() is not None


def has_host_participation(cursor, match_id):
    cursor.execute(
        """
        SELECT COUNT(*)
        FROM `friendly_match_participation`
        WHERE Friendly_Match_Id = %s AND Role = 'host'
        """,
        (match_id,),
    )
    return cursor.fetchone()[0] > 0


def insert_solo_request(cursor, game_user_id, match_id, comment, status):
    cursor.execute(
        """
        INSERT INTO `friendly_match_request` (Game_user_Id, Friendly_Match_Id, Comment, status)
        VALUES (%s, %s, %s, %s)
        """,
        (game_user_id, match_id, comment, status),
    )
    request_id = cursor.lastrowid
    cursor.execute(
        """
        INSERT INTO `friendly_match_request_participation` (Friendly_match_request_id, Game_user_Id)
        VALUES (%s, %s)
        """,
        (request_id, game_user_id),
    )


def seed_friendly_match_request_solo():
    approve_rate_under_recruit = env_float("SEED_SOLO_APPROVE_RATE_UNDER_RECRUIT", 0.5)
    approve_rate_over_recruit = env_float("SEED_SOLO_APPROVE_RATE_OVER_RECRUIT", 0.7)
    await_rate = env_float("SEED_SOLO_AWAIT_RATE", 0.9)

    with db_cursor() as (_, cursor):
        users_by_game = grouped_values(fetch_all(cursor, "SELECT Game_Id, Id FROM `game_user`"))
        matches = fetch_all(cursor, "SELECT Id, Game_Id, Host_Id, Date, Time, Recruit FROM `friendly_match` WHERE Sort = 1")

        for match_id, game_id, host_id, match_date, match_time, recruit in matches:
            candidates = [user_id for user_id in users_by_game.get(game_id, []) if user_id != host_id]
            if not candidates:
                continue

            adjusted_recruit = max(recruit - (1 if has_host_participation(cursor, match_id) else 0), 0)
            over_recruit = random.random() >= 0.5
            request_count = random.randint(0, adjusted_recruit) if not over_recruit else random.randint(adjusted_recruit, adjusted_recruit * 2)
            approve_rate = approve_rate_over_recruit if over_recruit else approve_rate_under_recruit

            approved_count = 0
            random.shuffle(candidates)
            for game_user_id in candidates[:request_count]:
                if has_existing_schedule(cursor, game_user_id, match_date, match_time):
                    continue

                if approved_count < adjusted_recruit and random.random() < approve_rate:
                    status = "approve"
                    approved_count += 1
                else:
                    status = "await" if random.random() < await_rate else "reject"

                if approved_count >= adjusted_recruit and status == "await":
                    status = "reject"

                try:
                    insert_solo_request(cursor, game_user_id, match_id, fake.text(max_nb_chars=30), status)
                except mysql.connector.IntegrityError:
                    continue


def seed_friendly_match_request_team():
    requests_per_team_match_max = env_int("SEED_REQUESTS_PER_TEAM_MATCH_MAX", 5)
    team_approve_rate = env_float("SEED_TEAM_APPROVE_RATE", 0.2)

    with db_cursor() as (_, cursor):
        users_by_game = grouped_values(fetch_all(cursor, "SELECT Game_Id, Id FROM `game_user`"))
        matches = fetch_all(cursor, "SELECT Id, Game_Id, Host_Id, Date, Time FROM `friendly_match` WHERE Sort = 0")

        for match_id, game_id, host_id, match_date, match_time in matches:
            candidates = [user_id for user_id in users_by_game.get(game_id, []) if user_id != host_id]
            if not candidates:
                continue

            random.shuffle(candidates)
            request_count = min(random.randint(0, requests_per_team_match_max), len(candidates))
            has_approved = False

            for game_user_id in candidates[:request_count]:
                if has_existing_schedule(cursor, game_user_id, match_date, match_time):
                    continue

                if not has_approved and random.random() < team_approve_rate:
                    status = "approve"
                    has_approved = True
                else:
                    status = "reject" if has_approved else "await"

                try:
                    cursor.execute(
                        """
                        INSERT INTO `friendly_match_request` (Game_user_Id, Friendly_Match_Id, Comment, status)
                        VALUES (%s, %s, %s, %s)
                        """,
                        (game_user_id, match_id, fake.text(max_nb_chars=30), status),
                    )
                except mysql.connector.IntegrityError:
                    continue


def seed_friendly_match_request_participation_team():
    with db_cursor(buffered=True) as (_, cursor):
        users_by_game = grouped_values(fetch_all(cursor, "SELECT Game_Id, Id FROM `game_user`"))
        matches = fetch_all(cursor, "SELECT Id, Game_Id, Host_Id, Date, Time, Recruit FROM `friendly_match` WHERE Sort = 0")

        for match_id, game_id, host_id, match_date, match_time, recruit in matches:
            requests = fetch_all(
                cursor,
                "SELECT Id, Game_user_Id FROM `friendly_match_request` WHERE Friendly_Match_Id = %s",
                (match_id,),
            )
            candidates = [user_id for user_id in users_by_game.get(game_id, []) if user_id != host_id]

            for request_id, requester_id in requests:
                participants = []
                if not has_existing_schedule(cursor, requester_id, match_date, match_time):
                    participants.append(requester_id)

                random.shuffle(candidates)
                for user_id in candidates:
                    if len(participants) >= recruit:
                        break
                    if user_id in participants or has_existing_schedule(cursor, user_id, match_date, match_time):
                        continue
                    participants.append(user_id)

                if participants:
                    cursor.executemany(
                        """
                        INSERT IGNORE INTO `friendly_match_request_participation` (Friendly_match_request_id, Game_user_id)
                        VALUES (%s, %s)
                        """,
                        [(request_id, participant_id) for participant_id in participants],
                    )


def seed_friendly_match_participation_client():
    with db_cursor() as (_, cursor):
        approved_requests = fetch_all(
            cursor,
            """
            SELECT Id, Friendly_Match_Id
            FROM `friendly_match_request`
            WHERE status = 'approve'
            """,
        )

        for request_id, match_id in approved_requests:
            participants = fetch_all(
                cursor,
                """
                SELECT Game_user_id
                FROM `friendly_match_request_participation`
                WHERE Friendly_match_request_id = %s
                """,
                (request_id,),
            )
            if not participants:
                continue

            cursor.executemany(
                """
                INSERT INTO `friendly_match_participation` (Game_User_Id, Friendly_Match_Id, Role)
                VALUES (%s, %s, 'client')
                ON DUPLICATE KEY UPDATE Role = VALUES(Role)
                """,
                [(game_user_id, match_id) for (game_user_id,) in participants],
            )


def seed_friendly_match_state_change():
    with db_cursor() as (_, cursor):
        cursor.execute(
            """
            UPDATE `friendly_match` fm
            SET State = 1
            WHERE fm.Sort = 0
            AND fm.State = 0
            AND EXISTS (
                SELECT 1
                FROM `friendly_match_participation` fmp
                WHERE fmp.Friendly_Match_Id = fm.Id
                AND fmp.Role = 'client'
            )
            """
        )

        cursor.execute(
            """
            UPDATE `friendly_match` fm
            SET State = 1
            WHERE fm.Sort = 1
            AND fm.State = 0
            AND (
                SELECT COUNT(*)
                FROM `friendly_match_participation` fmp
                WHERE fmp.Friendly_Match_Id = fm.Id
            ) >= fm.Recruit
            """
        )


SEED_STEPS = [
    ("users", seed_users),
    ("groups", seed_groups),
    ("game users", seed_game_users),
    ("posts", seed_posts),
    ("comments", seed_comments),
    ("friendships", seed_friendships),
    ("friendly matches", seed_friendly_matches),
    ("solo host participation", seed_friendly_match_participation_solo),
    ("team host participation", seed_friendly_match_participation_team),
    ("solo match requests", seed_friendly_match_request_solo),
    ("team match requests", seed_friendly_match_request_team),
    ("team request participants", seed_friendly_match_request_participation_team),
    ("approved client participation", seed_friendly_match_participation_client),
    ("friendly match state", seed_friendly_match_state_change),
]


def seed_all():
    for name, function in SEED_STEPS:
        print(f"[seed] {name}", flush=True)
        function()


def parse_args():
    parser = argparse.ArgumentParser(description="Create the Game Match database and generate Faker seed data.")
    parser.add_argument(
        "--schema-file",
        default=str(DEFAULT_SCHEMA_PATH),
        help="SQL schema file to execute. Defaults to src/main/game_match.sql.",
    )
    parser.add_argument(
        "--skip-schema",
        action="store_true",
        help="Do not recreate the database schema before seeding.",
    )
    parser.add_argument(
        "--skip-seed",
        action="store_true",
        help="Create the schema only and skip Faker seed data.",
    )
    parser.add_argument(
        "--reset-seed",
        action="store_true",
        help="When --skip-schema is used, clear seed tables before inserting data.",
    )
    return parser.parse_args()


def main():
    load_env_file(PROJECT_ROOT / ".env")
    load_env_file(MAIN_DIR / ".env")

    args = parse_args()
    schema_path = Path(args.schema_file).expanduser().resolve()

    if args.skip_schema and args.skip_seed:
        print("Nothing to do: both --skip-schema and --skip-seed were passed.", file=sys.stderr, flush=True)
        return 1

    if not args.skip_schema:
        if not schema_path.exists():
            print(f"Schema file not found: {schema_path}", file=sys.stderr, flush=True)
            return 1
        print(f"[schema] recreate database from {schema_path}", flush=True)
        execute_schema(schema_path)
    elif args.reset_seed:
        print("[reset] clear seed tables", flush=True)
        reset_seed_tables()

    if not args.skip_seed:
        seed_all()

    print("[done] database setup completed", flush=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
