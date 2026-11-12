# VanillaCommandAdvance

Плагин для Paper и Folia, который заменяет ванильные `/gamemode` и `/tp` на свои версии: отдельное право на каждый режим и каждый вид телепорта, свои алиасы и сообщения, которые можно переписать под сервер.

Версии: 1.21.x, 26.1, 26.1.x, 26.x

## Русский

### /gamemode

Пишется как обычно: `/gamemode <режим> [игроки]`. Режим можно ввести как удобно: `creative`, `c` или `1`. По умолчанию есть алиас `/gm`, так что хватит и `/gm c`. В подсказках игрок видит только те режимы, на которые у него есть право.

| Право | Что даёт |
|---|---|
| `vanillacommandadvance.gamemode.survival` | Режим выживания |
| `vanillacommandadvance.gamemode.creative` | Творческий режим |
| `vanillacommandadvance.gamemode.adventure` | Режим приключения |
| `vanillacommandadvance.gamemode.spectator` | Режим наблюдателя |
| `vanillacommandadvance.gamemode.others` | Менять режим другим игрокам |

### /tp

Работают все ванильные варианты: к игроку, по координатам (в том числе с `~` и `^`), одного игрока к другому, селекторы `@a` и `@e`. Сверх ванилы после координат можно указать мир: `/tp 0 100 0 world_nether`.

По умолчанию сообщение о телепорте видит только тот, кто ввёл команду. За это отвечает параметр `silent-target`.

| Право | Что даёт |
|---|---|
| `vanillacommandadvance.tp.self` | Телепортироваться самому |
| `vanillacommandadvance.tp.others` | Телепортировать других игроков и сущности |
| `vanillacommandadvance.tp.coords` | Телепортироваться по координатам |
| `vanillacommandadvance.tp.worlds` | Телепортироваться между мирами |
| `vanillacommandadvance.tp.silent` | Телепортированному никогда не приходит сообщение |

### Конфиг

Любую из команд можно выключить, тогда на сервере снова работает ванильная. Для каждой команды задаются свои алиасы.

Все сообщения лежат в `messages.yml` и пишутся в MiniMessage. Можно использовать плейсхолдеры `<player>`, `<target>`, `<sender>`, `<gamemode>`, `<x>`, `<y>`, `<z>` и `<world>`.

`/vca reload` перечитывает сообщения и настройки без рестарта. Только включение команд и алиасы вступают в силу после рестарта.

### Folia и API

На Folia смена режима и телепорт выполняются в потоке региона игрока, как и положено. Телепорт идёт через `teleportAsync`, поэтому чанк подгружается, а сервер при этом не встаёт.

Плагин кидает два отменяемых события. В `CommandGameModeEvent` можно подменить режим, в `CommandTeleportEvent` — точку назначения.

## English

### /gamemode

Works the usual way: `/gamemode <mode> [players]`. The mode can be typed however you like: `creative`, `c` or `1`. There's a `/gm` alias by default, so `/gm c` is enough. Tab completion only shows the modes the player has permission for.

| Permission | What it allows |
|---|---|
| `vanillacommandadvance.gamemode.survival` | Survival mode |
| `vanillacommandadvance.gamemode.creative` | Creative mode |
| `vanillacommandadvance.gamemode.adventure` | Adventure mode |
| `vanillacommandadvance.gamemode.spectator` | Spectator mode |
| `vanillacommandadvance.gamemode.others` | Changing other players' mode |

### /tp

Every vanilla form works: to a player, by coordinates (including `~` and `^`), one player to another, and the `@a` and `@e` selectors. On top of vanilla, you can put a world after the coordinates: `/tp 0 100 0 world_nether`.

By default only the person who ran the command sees the teleport message. That's what `silent-target` controls.

| Permission | What it allows |
|---|---|
| `vanillacommandadvance.tp.self` | Teleporting yourself |
| `vanillacommandadvance.tp.others` | Teleporting other players and entities |
| `vanillacommandadvance.tp.coords` | Teleporting by coordinates |
| `vanillacommandadvance.tp.worlds` | Teleporting between worlds |
| `vanillacommandadvance.tp.silent` | The teleported player never gets a message |

### Config

Any of the commands can be turned off, and then the vanilla one is back on the server. Each command gets its own aliases.

Every message lives in `messages.yml` and is written in MiniMessage. You can use the placeholders `<player>`, `<target>`, `<sender>`, `<gamemode>`, `<x>`, `<y>`, `<z>` and `<world>`.

`/vca reload` reloads messages and settings without a restart. Only turning commands on or off and aliases need a restart to apply.

### Folia and API

On Folia, mode changes and teleports run on the player's region thread, the way they should. Teleports go through `teleportAsync`, so the chunk loads without stalling the server.

There are two cancellable events. In `CommandGameModeEvent` you can swap the mode, and in `CommandTeleportEvent` the destination.
