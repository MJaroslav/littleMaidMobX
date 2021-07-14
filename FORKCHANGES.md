# Fork changes

## Done

* Removed IDE files. _// Why it in repo?._
* Added IDE files to .gitignore _(Marked by my comment)_.
* Upped gradle wrapper version: __5.4.6__.
* Fixed ForgeGradle repo (used Sponge proxy repo). _// Thanks forge for their non-last Minecraft version technical support politics._
* Forced __JDK 1.8__.
* Build script reworked: removed guide comments, fixed archive name and version setter.
* Upped forge version: __10.13.4.1614__.
* Removed src/readme_jp.txt and drq.
* Fix trigger gui crash.
* Make configuration based on forge api.
  * Make auto configutation based on java reflection. _// May be port it to MJUtils lib._
  * Make GUI Configuration.
  * If maid has contract, she dont will despawn with enabled maid despawning.
  * Rewriten maid ignore list: can add mod ids and/or items by modid:name/modid:name:meta

## Planned

* More modes for maids.
* Global code clean up.
* Fix door open/close AI // Is not a bug, wild maids cant open doors. May be i add this ability later.
* Compatibility with mods.
  * Withery: rite of summoning (for maid spawn).
  * ThaumCraft: add aspects to maid Entity.
* Make configuration based on forge api.
  * Make more options for configuration.
    * Custom items for interactions with Maids.
    * Make custom drop after maid death.
    * Make toggle for mode-item and sugar consumability.
* Make localization for non localized strings.
  * Add russian localization.
* May be uploading to curse.
