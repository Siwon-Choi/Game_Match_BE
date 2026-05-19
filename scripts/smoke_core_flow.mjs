const baseUrl = process.env.SMOKE_BASE_URL || "http://127.0.0.1:18080";

const results = [];

function record(name, ok, detail = "") {
  results.push({ name, ok, detail });
}

async function request(method, path, { token, body, multipart } = {}) {
  const headers = {};
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  let requestBody;
  if (multipart) {
    requestBody = multipart;
  } else if (body !== undefined) {
    headers["Content-Type"] = "application/json";
    requestBody = JSON.stringify(body);
  }

  const response = await fetch(`${baseUrl}${path}`, {
    method,
    headers,
    body: requestBody,
  });

  const text = await response.text();
  let json = null;
  try {
    json = text ? JSON.parse(text) : null;
  } catch {
    json = null;
  }

  if (!response.ok) {
    const message = json?.message || text || `HTTP ${response.status}`;
    const error = new Error(message);
    error.status = response.status;
    error.body = json || text;
    throw error;
  }

  return json?.data ?? json ?? text;
}

function futureDate(daysFromNow) {
  const date = new Date();
  date.setDate(date.getDate() + daysFromNow);
  return date.toISOString().slice(0, 10);
}

async function main() {
  const games = await request("GET", "/games");
  record("GET /games", Array.isArray(games), `count=${Array.isArray(games) ? games.length : "n/a"}`);
  const game = Array.isArray(games) ? games[0] : null;
  if (!game) {
    throw new Error("No game seed data. Add at least one row to Game.");
  }

  const suffix = Date.now().toString().slice(-9);
  const loginId = `smoke${suffix}`;
  const password = "abc12345";
  const userPayload = {
    loginId,
    loginPassword: password,
    email: `smoke${suffix}@example.com`,
    name: "Smoke User",
    birth: "2000-01-01",
    phoneNumber: `010${suffix.slice(-8)}`,
  };

  const registered = await request("POST", "/register", { body: userPayload });
  const token = registered.accessToken;
  const refreshToken = registered.refreshToken;
  const userId = registered.userId;
  record("POST /register", Boolean(token && refreshToken && userId), `userId=${userId}`);

  const currentUser = await request("GET", "/user", { token });
  record("GET /user", currentUser.id === userId, `id=${currentUser.id}`);

  const rotated = await request("POST", "/token/refresh", { body: { refreshToken } });
  let accessToken = rotated.accessToken;
  let currentRefreshToken = rotated.refreshToken;
  record("POST /token/refresh", Boolean(accessToken && currentRefreshToken), "rotated=true");

  try {
    await request("POST", "/phone-verifications", {
      body: { phoneNumber: userPayload.phoneNumber, sessionId: `session-${suffix}` },
    });
    record("POST /phone-verifications", true, "configured");
  } catch (error) {
    record(
      "POST /phone-verifications",
      error.status === 503,
      `expected local failure: ${error.status} ${error.message}`,
    );
  }

  const gameUser = await request("POST", "/game-users", {
    token: accessToken,
    body: {
      gameId: game.id,
      nickname: `smokeNick${suffix}`,
      groupId: null,
    },
  });
  record("POST /game-users", Boolean(gameUser.id), `gameUserId=${gameUser.id}`);

  const fetchedGameUser = await request("GET", `/game-users/${gameUser.id}`);
  record("GET /game-users/{id}", fetchedGameUser.id === gameUser.id, `nickname=${fetchedGameUser.nickname}`);

  const post = await request("POST", "/posts", {
    token: accessToken,
    body: {
      title: "Smoke Test",
      content: "hello smoke",
      anonymous: false,
      gameId: game.id,
      userId,
    },
  });
  record("POST /posts", Boolean(post.id), `postId=${post.id}`);

  const postDetail = await request("GET", `/posts/${post.id}`);
  record("GET /posts/{id}", postDetail.id === post.id, `title=${postDetail.title}`);

  await request("POST", `/posts/${post.id}/views`);
  record("POST /posts/{id}/views", true, "ok");

  const images = await request("GET", `/posts/${post.id}/images`);
  record("GET /posts/{id}/images", Array.isArray(images), `count=${images.length}`);

  const comment = await request("POST", `/posts/${post.id}/comments`, {
    token: accessToken,
    body: {
      userId,
      content: "smoke comment",
      date: "2026-05-11",
      time: "16:30:00",
      parentCommentId: null,
      anonymous: false,
    },
  });
  record("POST /posts/{id}/comments", Boolean(comment.id), `commentId=${comment.id}`);

  const reply = await request("POST", `/posts/${post.id}/comments`, {
    token: accessToken,
    body: {
      userId,
      content: "smoke reply",
      date: "2026-05-11",
      time: "16:31:00",
      parentCommentId: comment.id,
      anonymous: false,
    },
  });
  record("POST reply comment", Boolean(reply.id), `replyId=${reply.id}`);

  const comments = await request("GET", `/posts/${post.id}/comments`);
  record("GET /posts/{id}/comments", comments.some((item) => item.id === comment.id), `count=${comments.length}`);

  const replies = await request("GET", `/comments/${comment.id}/replies`);
  record("GET /comments/{id}/replies", replies.some((item) => item.id === reply.id), `count=${replies.length}`);

  const voteStatus = await request("GET", `/posts/${post.id}/votes/me`, { token: accessToken });
  record("GET /posts/{id}/votes/me", voteStatus === "No recommendation exists", voteStatus);

  const voteResult = await request("POST", `/posts/${post.id}/votes`, {
    token: accessToken,
    body: { goodOrBad: true },
  });
  record("POST /posts/{id}/votes", voteResult.status === "Recommended", `status=${voteResult.status}`);

  const friendlyMatch = await request("POST", "/friendly-matches", {
    token: accessToken,
    body: {
      hostId: gameUser.id,
      gameId: game.id,
      date: futureDate(7),
      time: "20:00:00",
      sort: 1,
      state: 0,
      recruit: 1,
      comment: "smoke match",
    },
  });
  record("POST /friendly-matches", Boolean(friendlyMatch.id), `matchId=${friendlyMatch.id}`);

  const matchDetail = await request("GET", `/friendly-matches/${friendlyMatch.id}`);
  record("GET /friendly-matches/{id}", matchDetail.id === friendlyMatch.id, `date=${matchDetail.date}`);

  const participation = await request("POST", `/friendly-matches/${friendlyMatch.id}/participations`, {
    token: accessToken,
    body: {
      gameUserId: gameUser.id,
      role: "host",
    },
  });
  record("POST /friendly-matches/{id}/participations", Boolean(participation.id), `id=${participation.id}`);

  const roster = await request("GET", `/friendly-matches/${friendlyMatch.id}/participations`);
  record("GET /friendly-matches/{id}/participations", Array.isArray(roster), `count=${roster.length}`);

  const conflict = await request(
    "GET",
    `/friendly-matches/${friendlyMatch.id}/requests/conflict-check?userId=${userId}`,
  );
  record("GET requests conflict-check", typeof conflict === "string", conflict);

  const matchRequestId = await request("POST", `/friendly-matches/${friendlyMatch.id}/requests`, {
    token: accessToken,
    body: {
      gameUserId: gameUser.id,
      teamMemberIds: [],
      comment: "smoke request",
    },
  });
  record("POST /friendly-matches/{id}/requests", Number.isInteger(matchRequestId), `requestId=${matchRequestId}`);

  const requestDetail = await request("GET", `/match-requests/${matchRequestId}`);
  record(
    "GET /match-requests/{id}",
    requestDetail.requestId === matchRequestId,
    `requestId=${requestDetail.requestId}, status=${requestDetail.status}`,
  );

  await request("POST", "/logout", { body: { refreshToken: currentRefreshToken } });
  record("POST /logout", true, "ok");
}

try {
  await main();
} catch (error) {
  record("FLOW_ABORTED", false, `${error.status || ""} ${error.message}`.trim());
}

const failed = results.filter((result) => !result.ok);
for (const result of results) {
  const status = result.ok ? "PASS" : "FAIL";
  console.log(`${status} ${result.name}${result.detail ? ` - ${result.detail}` : ""}`);
}

if (failed.length > 0) {
  process.exitCode = 1;
}
