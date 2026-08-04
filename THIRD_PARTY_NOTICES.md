# Third-Party Notices

Neuro Karaoke as a whole is distributed under the **GNU General Public License,
version 3.0** (see [LICENSE](LICENSE)). This file records the third-party works
incorporated into the project and their licenses.

---

## GPL-3.0 components (copyleft — the reason this project is GPLv3)

### NeurolingsCE mascot packs
- **What:** The "Neurolings" desktop-pet mascot packs bundled under
  `Android/app/src/main/assets/mascots/` — `Cerber`, `Eviling`, `Neuron`,
  `Tuteling`, `Vedaling`, `Weuron` (sprite frames + `actions.xml` / `behaviors.xml`).
- **Source project:** NeurolingsCE ("Custom Edition"), maintained by **Luda** — a
  Neuro-themed edition in the Neurolings / Shimeji lineage.
- **License:** GNU General Public License v3.0.
- **Project references:**
  - Neurolings project: https://neurofumo.itch.io/neurolings
  - NeurolingsCE source repository: https://github.com/qingchenyouforcc/NeurolingsCE
- **Per-pack credits** (each pack also ships its original `info.json` credits,
  bundled unmodified):
  - `Neuron`, `Weuron`, `Eviling` — art by **Paccha** (https://pacchacomms.carrd.co/);
    configuration by **@promote.** and **@dalekcraft**
  - `Tuteling`, `Vedaling` — art by **Moneka** (https://linktr.ee/monikaphobia);
    configuration by **@promote.** and **@dalekcraft**
  - `Cerber` — art by **AnoWan, 于陌Wan, 六水Wan, 加斯科涅Wan, 士灰Wan** (bilibili);
    configuration by **qingchenyou (@轻尘呦)**
- **Note:** Redistributed here under the same GPL-3.0 terms.

### Shimeji-ee (animation engine)
- **What:** The Kotlin Neurolings animation engine in
  `Android/app/src/main/java/com/soul/neurokaraoke/ui/tv/neurolings/` is a **port
  of the Shimeji-ee desktop-pet engine** (XML action/behavior model, the `${…}`
  expression subset, the action/behavior state machine, and the standard Shimeji
  physics constants).
- **Upstream:** Shimeji-ee © **Kilkakon**; based on the original **Shimeji** by
  **Group Finity** (Yuki Yamada).
- **License:** GNU General Public License v3.0.

Because the above are GPL-3.0 and are combined into this application, the
combined work is licensed under GPL-3.0.

---

## Permissively licensed dependencies

These are used under their own terms; their licenses do not impose copyleft on
the project.

**Android app**
- AndroidX / Jetpack (Core, Lifecycle, Activity, Navigation, Compose, Compose for
  TV, Media3/ExoPlayer, ProfileInstaller, Benchmark, Car App Library) — Apache-2.0
- Coil — Apache-2.0
- Kotlin / kotlinx-coroutines — Apache-2.0
- org.json (unit tests) — JSON License / Apache-2.0

**Desktop (Electron wrapper)**
- Electron — MIT
- electron-updater / electron-builder — MIT
- discord-rpc — MIT
- lodash — MIT

---

## Notes

- Full license texts: GPL-3.0 is in [LICENSE](LICENSE). Apache-2.0 and MIT texts
  are available from the respective projects.
- If any attribution here is incomplete or a source URL needs correcting, please
  open an issue or contact the maintainer — corrections are welcome.
