const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "gen-lang-client-0318136037";
const ALICE_UID = "user_alice_123";
const BOB_UID = "user_bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validEntry(userId, overrides = {}) {
  const past = new Date(Date.now() - 5000);
  return {
    userId,
    displayName: "Zeynep Yılmaz",
    avatarColorIndex: 2,
    grade: 8,
    totalPoints: 450,
    weeklyPoints: 180,
    weekKey: "2026-W47",
    streakCount: 4,
    perfectQuizCount: 2,
    activeMultiplier: 5,
    publicVisible: true,
    createdAt: past,
    updatedAt: past,
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read, list, or write leaderboard entries", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("leaderboard").doc(ALICE_UID).set(validEntry(ALICE_UID))
  );
  await assertFails(unauthDb.collection("leaderboard").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("leaderboard").where("publicVisible", "==", true).get()
  );
});

test("Authenticated owner: valid Kotlin create, partial update, filtered list, and delete succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const aliceRef = aliceDb.collection("leaderboard").doc(ALICE_UID);

  // Mirror LeaderboardRepository.upsertCurrentUserLeaderboard create payload (13 keys, no @DocumentId `id`)
  await assertSucceeds(aliceRef.set(validEntry(ALICE_UID)));
  await assertSucceeds(aliceRef.get());

  // Mirror LeaderboardRepository.observeLeaderboard query: .whereEqualTo("publicVisible", true).limit(100)
  await assertSucceeds(
    aliceDb
      .collection("leaderboard")
      .where("publicVisible", "==", true)
      .limit(100)
      .get()
  );

  // Mirror partial update payload preserving immutable createdAt and userId
  await assertSucceeds(
    aliceRef.update({
      displayName: "Zeynep Y.",
      avatarColorIndex: 4,
      grade: 9,
      totalPoints: 1250,
      weeklyPoints: 980,
      weekKey: "2026-W47",
      streakCount: 5,
      perfectQuizCount: 3,
      activeMultiplier: 10,
      publicVisible: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertSucceeds(aliceRef.delete());
});

test("Unfiltered top-level list query without publicVisible filter is denied", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set(validEntry(ALICE_UID))
  );
  await assertFails(aliceDb.collection("leaderboard").get());
});

test("Cross-user write, update, and delete are denied while public leaderboard read succeeds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set(validEntry(ALICE_UID))
  );

  // Bob can read Alice's public leaderboard entry
  await assertSucceeds(bobDb.collection("leaderboard").doc(ALICE_UID).get());
  await assertSucceeds(
    bobDb.collection("leaderboard").where("publicVisible", "==", true).get()
  );

  // Bob cannot overwrite, update, or delete Alice's entry
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).set(validEntry(ALICE_UID))
  );
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).update({
      totalPoints: 999999,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(bobDb.collection("leaderboard").doc(ALICE_UID).delete());
});

test("Rejects ghost keys, @DocumentId id field, invalid grade, future timestamps, and immutable createdAt change", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const aliceRef = aliceDb.collection("leaderboard").doc(ALICE_UID);

  // Ghost key
  await assertFails(aliceRef.set(validEntry(ALICE_UID, { adminRole: true })));
  // Redundant @DocumentId `id` field
  await assertFails(aliceRef.set(validEntry(ALICE_UID, { id: ALICE_UID })));
  // Out-of-bounds grade
  await assertFails(aliceRef.set(validEntry(ALICE_UID, { grade: 13 })));
  // Invalid weekKey format
  await assertFails(aliceRef.set(validEntry(ALICE_UID, { weekKey: "2026-99" })));
  // Future timestamp
  await assertFails(
    aliceRef.set(
      validEntry(ALICE_UID, { createdAt: new Date(Date.now() + 60000) })
    )
  );

  // Create valid doc first
  await assertSucceeds(aliceRef.set(validEntry(ALICE_UID)));

  // Reject mutating immutable createdAt
  await assertFails(
    aliceRef.update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject adding PII field on update
  await assertFails(
    aliceRef.update({
      email: "student@example.com",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});
