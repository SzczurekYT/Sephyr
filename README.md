# ✨ Sephyr
**Sephyr is a voice spellcasting mod, where you cast spells by speaking out loud magic chants in a fictional language.** Instead of pressing keybinds or using spellbooks, all you need to cast your first spell, is knowing of what to say.<br>

---
⚠️ PROTOTYPE WARNING - This project is on a proof of concept stage. I want to let others test if the idea of spoken magic can work well and be fun, but aside, there isn't much content in the mod yet.

---
## ❓Spellcasting - How To
Sephyr works by capturing audio from your microphone, and detecting keywords in it. To cast a spell you need to say a specific sequences of words. Each detected word will display above your hotbar.

![Spell word alɪˈvɑn above player's hotbar](https://cdn.modrinth.com/data/cached_images/f8f075848a107bb0a05331196241191f0b6e0d96.png)<br>

After you finish talking, if the said sequence is correct, a matching spell will activate. To succesfully cast spells you need to:
- Speak calmly and clearly.
- Say everything consecutively, as a long pause between the words will break the chant or activate an unfinished spell.

If the spell fails, wait a second and try saying it again. Be aware that the system sometimes mistakenly detects words that weren't said.

See below for a list of spells, and a troubleshooting guide if you are having issues.

# 📥 Installation Guide
You need to download the [mod](https://modrinth.com/mod/sephyr/versions), and its dependencies - [Balm](https://modrinth.com/mod/balm/versions), [Photon](https://modrinth.com/mod/photon-editor/versions) and [LDLib](https://modrinth.com/mod/ldlib/versions).<br>
To use the mod you need a working microphone.<br>
You also need around 260MB of free disk space. On first run Sephyr downloads a speech recognition model of this size, and run it on your computer. It's what makes it possible to detect the spoken magic words.<br>
Once you are in a world you should be able to start casting right away. If words are not detected use `/sephyr status` to check what microphone is in use. If it's wrong type `/sephyr microphone `, use tab autocomplete to select a correct mirophone, and run the command.
# 🪄 Spell list
Those are all spells currently available in the mod. They are written in [International Phonetic Alphabet](https://en.wikipedia.org/wiki/International_Phonetic_Alphabet), used to describe pronunciation. You might have seen it in a dicitonary or a foreign language textbook. Currently there are only 3 of them, two of which have variants. All are wind ones, so the first word is always `alɪˈvɑn`.<br>
Use this list to learn how to pronounce them
<details>
<summary>Word pronouncation list</summary>
  
  Note: The list intentionally uses different voices for different words, because some pronounce it better.
- [alɪˈvɑn](https://ipa-reader.com/?text=al%C9%AA%CB%88v%C9%91n&voice=Bianca)
- [ˈdɪrɛka](https://ipa-reader.com/?text=%CB%88d%C9%AAr%C9%9Bka&voice=Hans)
- [ɛkɐsuɾɯ](https://ipa-reader.com/?text=%C9%9Bk%C9%90su%C9%BE%C9%AF&voice=Zeina)
- [ãntikɔ](https://ipa-reader.com/?text=%C3%A3ntik%C9%94&voice=Bianca)
- [wˈanga](https://ipa-reader.com/?text=w%CB%88anga&voice=Lea)
- [vɛɾtɛɾ](https://ipa-reader.com/?text=v%C9%9B%C9%BEt%C9%9B%C9%BE&voice=Penelope)
- [ɛvi](https://ipa-reader.com/?text=%C9%9Bvi&voice=Tatyana) - note: this word doesn't get detected quite often
- [ˈɔnːdˌɔː](https://ipa-reader.com/?text=%CB%88%C9%94n%CB%90d%CB%8C%C9%94%CB%90&voice=Ruben)
- [fɒksɑm](https://ipa-reader.com/?text=f%C9%92ks%C9%91m&voice=Emma)
  
</details>


### Wind beam
Conjures a flow of air in front of the player, pushing entities in it's range away.<br>
Base chant: `alɪˈvɑn ˈdɪrɛka`<br>
It can be followed with `ɛkɐsuɾɯ` to make the the stream of air bigger at the cost of speed and range. It can be also followed with `ãntikɔ` to make the air pull instead of pushing. Combination of them also works, so saying `alɪˈvɑn ˈdɪrɛka ɛkɐsuɾɯ ãntikɔ` will create a wide stream of air pulling entites toward the player.
### Wind leap
Launches you forward/back/up depending on the last word. It can also be ommited, resulting in the same effect as forward, but with a slight delay.<br>
Chant: `alɪˈvɑn vɛɾtɛɾ ɛvi [ˈɔnːdˌɔː / ãntikɔ / fɒksɑm]`
### Wind shot
Shoots a wind charge. Will be made cooler in the future.<br>
Chant: `alɪˈvɑn wˈanga`

# 🛠️ Troubleshooting Guide
If the detection ins't working for you run the `/sephyr status` command and ensure it looks like this:

![Output of the sephyr status command, showing the detection is running and the microphone is correct](https://cdn.modrinth.com/data/cached_images/3db30bf7684e2e23167049f599b52906cbca40a3.png)
If the microphone is wrong, type `/sephyr microphone `, use tab autocomplete to select a correct mirophone, and run the command.<br>
If the command says it's not running or loading isn't finished check the logs.
Ensure:
- You are pronouncing the words correctly. Don't rush (but also don't be too slow).
- There is no music or noise (like a vacuum cleaner) that the microphone could be catching, which might disturb the detection.
- You don't have any weird effects / processing set up on your microphone.

# Code
This repository hosts all the Minecraft mod code. The keyword detection engine is called Sepple and it's kept in a separate [repository](https://github.com/SzczurekYT/Sepple). Bindings to it are located in the `sepple` directory.<br>
The mod is using a multiloader architecture, because a Fabric support is planned (see #1).

### Building Rust (Sepple) code
1. Install [Docker](https://www.docker.com/get-started/)
2. Run `gradlew sepple:buildDockerImages` (only has to be done once)
3. Run `gradlew sepple:buildRustNatives`

### Acknowledgments
- [Sable](https://github.com/ryanhcode/sable) for serving as a reference for gluing Rust code with Java.
