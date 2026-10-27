# silvermons-portrait-icons

Client-side Fabric mod for the Silvermons Cobblemon server. Item-converted Pokémon (the `cobblemon:pokemon_item` component on `cobblemon:pokemon_model`) are drawn in inventory/GUI slots as Cobblemon's own party-menu portrait, cropped to the 16x16 slot, so large models no longer overlap neighbouring slots. Hand, ground, item-frame and head rendering are untouched.

- Minecraft 1.21.1, Fabric Loader 0.16 or newer, Cobblemon 1.8.2 or newer.
- Install: put the jar in the client `mods` folder. The server needs nothing.
- Species, aspects (shiny/forms) and tint are read from the item's `cobblemon:pokemon_item` data.
- If rendering ever errors, the mod logs once and falls back to Cobblemon's normal 3D model.

## Config: `config/silvermons_slot_icons.json` (created on first launch)

| key | meaning |
|---|---|
| `enabled` | master switch |
| `showBackground` | draw Cobblemon's party portrait backdrop behind the icon |
| `scale`, `offsetX`, `offsetY` | global size multiplier / nudge in slot pixels |
| `overrides` | `"species"` or `"species\|aspect"` -> `{scale, offsetX, offsetY}`; most specific wins |
| `keepModel` | list of `"species"` / `"species\|aspect"` that keep the normal 3D model |

## In-game controls

- **Keybind** (Controls > Silvermons Slot Icons): toggles portrait icons and Cobblemon's default 3D models. Unbound by default; the state is saved to the config.
- **Config screen**: if Mod Menu and Cloth Config are installed, a config button appears for this mod. Neither is required; without them, edit the JSON file.
- Per-Pokémon adjustments in the screen use the format `species:scale,offsetX,offsetY`.

## Build

`gradlew build` with JDK 21. Cobblemon is fetched from the Cobblemon Maven as a compile-only dependency (version in `gradle.properties`) and is never bundled. The jar is written to `build/libs`.
