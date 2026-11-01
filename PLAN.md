# Pizza Empire – production plan

| Week | Goal | Status in this project |
|------|------|------------------------|
| 1 | Design, economy numbers, project setup | done |
| 2 | Game loop, isometric projection, player walking | done (`GameView`, `Iso`, `Carrier`) |
| 3 | Ovens, counter, carrying, cash pile | done (`World`, `Oven`, `Counter`, `MoneyPile`) |
| 4 | Customers: queue, seats, patience, state machine | done (`Customer`) |
| 5 | HUD in XML, shop, upgrades, helpers, saving | done (`ShopActivity`, `Upgrade`, `SaveManager`) |
| 6 | Sound, tutorial, settings, icon | done (`SoundManager`, dialogs, `SettingsActivity`) |
| 6b | Accounts: sign up, login, players DB, leaderboard | done (`PlayerDb`, `Auth`, `Session`, `LoginActivity`, `SignUpActivity`, `LeaderboardActivity`) |
| 7 | Playtest on a real device + balancing | **you**: tune `GameState` / `Upgrade` numbers |
| 8 | Sprites/polish, signed release, store listing | next |

Later ideas: Clean Bot, drive-thru lane, map of several restaurants, daily rewards (gems), staff upgrades, sprites.
