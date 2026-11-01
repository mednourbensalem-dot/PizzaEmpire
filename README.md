# 🍕 Pizza Empire  (Java + XML only, no Kotlin, no libraries)

Open this folder in Android Studio (the one that contains `settings.gradle`), let Gradle sync, press Run.
Min SDK 24. Package: `com.pizzaempire.game` (installs next to the older "Pizza Tycoon" app).

## How the game plays
Tap or drag on the floor to walk. Stand near an **oven** to pick up pizzas, walk to the **purple counter**
to drop them, customers queue and buy, and leave **cash** on the green spot: walk over it to collect.
Spend money in the **SHOP** (capacity, speed, oven speed, 2nd oven, helpers, marketing, price, gem exchange).

## Read the code in this order (about 1 hour)
1. `GameView.java`   – the game loop: `onDraw` -> `world.update(dt)` -> draw. Touch -> walking target.
2. `Iso.java`        – flat (x, y) tiles  ->  isometric screen pixels. Also draws boxes, people, pizza stacks.
3. `Entity.java`     – base class: `update(dt)` + `draw(...)` + `depth()` (draw order).
4. `Carrier.java`, `Player.java`, `Helper.java` – walking, collision sliding, carrying, helper AI.
5. `World.java`      – the rules: spawning, oven -> carrier -> counter transfers, selling, collecting cash.
6. `Customer.java`   – a state machine: QUEUE -> TO_SEAT -> EATING -> LEAVING.
7. `Oven`, `Counter`, `CounterPart`, `Table`, `MoneyPile`, `FloatText` – the objects of the shop.
8. `GameState`, `SaveManager`, `Upgrade`, `ShopActivity` – saving and the upgrade shop.
9. XML: `activity_world.xml` shows the pattern "custom view + HUD on top" inside a `FrameLayout`.

## Exercises (each one teaches one idea)
- Make walking faster for a minute after collecting cash (a timer in `GameState`).
- Add a third oven: another position in `World.sync()` + a shop item.
- Make a customer that orders 2 pizzas (a counter in `Customer`, `sell()` called twice).
- Add a "Clean bot" with a cooldown (store a timestamp in `SaveManager`).
- Replace the shape-drawn people with sprites: `canvas.drawBitmap` in `Iso.person`.

## Where things are tuned
`GameState.java` (speeds, prices, intervals) and `Upgrade.java` (costs). Customer patience: `Customer.java` (45 s).

## Accounts and players database (added)
- The app now starts at `LoginActivity`. `SignUpActivity` creates an account, `MainActivity` shows "Welcome, <name>",
  `LeaderboardActivity` lists the top 10 players, and Settings can reset progress or delete the account.
- `PlayerDb` (SQLite) has one `players` table: username (unique, not case sensitive), salt, password hash, timestamps
  and that player's saved game. Each player has their own save.
- `Auth` hashes passwords with salted PBKDF2 (the password itself is never stored) and validates input.
- `Session` remembers the logged-in player; `SaveManager` / `GameHolder` now load and save through the database.
- The database lives on the phone, so accounts exist per device. A shared online database needs a server (next step).

Read next: `Auth` -> `PlayerDb` -> `LoginActivity` / `SignUpActivity` -> `SaveManager`.
