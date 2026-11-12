# VanillaCommandAdvance

Plugin for **Paper** & **Folia** that replaces vanilla `/gamemode` and `/tp` with enhanced versions: flexible permissions, custom aliases, configurable messages.

**Versions:** 1.21.x, 26.1, 26.1.x, 26.x

---

# 🇷🇺 Русский

## 🎮 /gamemode

`/gamemode <режим> [игроки]`. Режим можно ввести как угодно: `creative`, `c`, `1`. Алиас по умолчанию `/gm`, так что работает и `/gm c`. В подсказках видны только режимы, на которые у игрока есть право.

### Права
- `vanillacommandadvance.gamemode.survival`
- `vanillacommandadvance.gamemode.creative`
- `vanillacommandadvance.gamemode.adventure`
- `vanillacommandadvance.gamemode.spectator`
- `vanillacommandadvance.gamemode.others` — смена режима другим игрокам

## 🧭 /tp

Все ванильные формы: к игроку, по координатам (в т.ч. `~` и `^`), игрок к игроку, селекторы `@a`, `@e`. После координат можно указать мир: `/tp 0 100 0 world_nether`. По умолчанию сообщение видит только отправитель (`silent-target`).

### Права
- `vanillacommandadvance.tp.self` — телепорт самого себя
- `vanillacommandadvance.tp.others` — телепорт других игроков и сущностей
- `vanillacommandadvance.tp.coords` — телепорт по координатам
- `vanillacommandadvance.tp.worlds` — телепорт между мирами
- `vanillacommandadvance.tp.silent` — телепортируемому никогда не приходит сообщение

## ⚙️ Конфигурация

- Любую команду можно отключить, тогда работает ванильная
- Свои алиасы для каждой команды
- Все тексты в `messages.yml` (MiniMessage, плейсхолдеры `<player>`, `<target>`, `<sender>`, `<gamemode>`, `<x>`, `<y>`, `<z>`, `<world>`)
- `/vca reload` перечитывает тексты и настройки без рестарта. Включение команд и алиасы применяются после рестарта

## 🚀 Folia / API

- Полная поддержка Folia: режим и телепорт выполняются в потоке региона игрока
- Телепорт через `teleportAsync`: чанк подгружается, не останавливая сервер
- Отменяемые события `CommandGameModeEvent` (режим можно подменить) и `CommandTeleportEvent` (точку назначения можно подменить)

---

# 🇬🇧 English

## 🎮 /gamemode

`/gamemode <mode> [players]`. Any input format works: `creative`, `c`, `1`. The default alias is `/gm`, so `/gm c` works too. Suggestions only show the modes the player has permission for.

### Permissions
- `vanillacommandadvance.gamemode.survival`
- `vanillacommandadvance.gamemode.creative`
- `vanillacommandadvance.gamemode.adventure`
- `vanillacommandadvance.gamemode.spectator`
- `vanillacommandadvance.gamemode.others` — change other players' mode

## 🧭 /tp

All vanilla forms: to a player, by coordinates (incl. `~` and `^`), player to player, `@a` and `@e` selectors. A world can follow the coordinates: `/tp 0 100 0 world_nether`. By default only the sender sees the message (`silent-target`).

### Permissions
- `vanillacommandadvance.tp.self` — teleport yourself
- `vanillacommandadvance.tp.others` — teleport other players and entities
- `vanillacommandadvance.tp.coords` — teleport by coordinates
- `vanillacommandadvance.tp.worlds` — teleport between worlds
- `vanillacommandadvance.tp.silent` — the teleported player never gets a message

## ⚙️ Configuration

- Any command can be disabled (falls back to vanilla)
- Custom aliases for every command
- All text in `messages.yml` (MiniMessage, placeholders `<player>`, `<target>`, `<sender>`, `<gamemode>`, `<x>`, `<y>`, `<z>`, `<world>`)
- `/vca reload` reloads texts and settings with no restart. Enabling commands and aliases apply after a restart

## 🚀 Folia / API

- Full Folia support: mode changes and teleports run on the player's region thread
- Teleports use `teleportAsync`: the chunk loads without stalling the server
- Cancellable `CommandGameModeEvent` (the mode can be replaced) and `CommandTeleportEvent` (the destination can be replaced)
