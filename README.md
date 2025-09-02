# 🎯 Discord multiple choice quiz Bot




A Discord quiz bot built in **Java** using **Discord4J** and **reactive streams**, designed primarily for **learning** through **A, B, C, D multiple-choice questions** — you're free to add any kind of categories you want.

---

## 🎯 Quiz Features

| 🧩 Feature                  | 📋 Description                                 |
|-----------------------------|------------------------------------------------|
| ✅ Multiple-choice format    | A, B, C, D answers                             |
| 🕒 Timed questions          | 30-second timers                               |
| 🔁 10-question rounds       | Questions asked back-to-back                   |
| 🧠 Dynamic countdowns       | Real-time countdown animation                  |
| 💬 Multiple quizzes         | One quiz per channel at a time                 |
| 🎮 In channels & DMs        | Play in public channels or in DMs              |
| 👥 Multiplayer & solo modes | Compete with others or play alone              |
| 📊 Scoreboards              | Track and compare player performance           |
| 🔖 Tagged question sets     | Tagging system for easy question sets creation |


<img src="https://i.imgur.com/gSGNl7k.gif" width="400" />&nbsp;&nbsp;<img src="https://i.imgur.com/9AZNKgb.gif" width="400" />
<img src="https://i.imgur.com/iJTj2pA.gif" width="400" />&nbsp;&nbsp;<img src="https://i.imgur.com/jQyieqy.gif" width="400" />

## 🛠 Getting Started

### 1. Clone the Repository

```
git clone https://github.com/yourusername/discord-quiz-bot.git
cd discord-quiz-bot
```

### 2. Setup Environment

- For docker environment, create *.env* file from the file *.env.example*:

```
cp .env.example .env
```
- Open *.env* and fill in your Discord bot token.

```
# .env
DISCORD_TOKEN=your_bot_token_here
```


### 3. Run using Java or Docker
Right here you can already run the bot without modifications, if you have docker. In the root folder run

```docker
docker compose up
```

If you want to run it in java without docker, remember to set the discord token in the environment, because you won't be using the *.env* file by default.

### 4. Questions' format
All questions you want to add will be in JSON format, as in the example below:
```
{
    "id": 1409186,
	"question": "Which brain structure is primarily involved in forming new memories?",
	"correctAnswers": ["Hippocampus"],
	"incorrectAnswers": ["Amygdala", "Cerebellum", "Thalamus"],
	"explanation": "The hippocampus is critical for the formation of new declarative memories, including episodic and semantic memory."
	"difficulty": 1
    "tags": ["brain", "neuroscience", "human memory"],
}
```
Questions are sorted automatically into levels by difficulty. The higher the difficulty, the higher the level at which question will appear.
Set difficulty to 1 for the easiest questions and 100 (or another number of your choice) for the hardest ones (difficulty level can repeat).

Tags are used to easily create question sets in your configuration file. They are not visible to the user, only for convenience in creation of sets.

More sample questions are in the */resources/sample/* folder, available to the bot by default.

### 5. Configuration folders and files

When you first clone the repo, the bot will use the configuration file (with specified question sets) in:
```
src/main/resources/sample/application-sample.yml
```
In this case, only JSON files with questions in */resources/sample/data/* will be loaded.

However, by default, the priority configuration is set to:
```
src/main/resources/private/application.yml
```
If this file exists, it will be loaded instead of the sample. Private folder is the folder to which you should eventually switch.
In this case, only JSON files with questions in */resources/private/data/* will be loaded.

At the beginning it's fine to use the sample folder. Eventually, you should create a */resources/private* folder with its own *data* folder and *application.yml* file and not use the */resources/sample* folder, which is just for starters.


Two configuration files are identical in use except for the name and the folder in which they are loaded, so you can just copy and modify the *application-sample.yml* to create *application.yml* for the private folder.


### 6. Add questions (JSON files)

First of all, in */resources/sample/data/* there is by default a bunch of sample questions with configuration.
You can start the bot as it is and see how it works, modify questions there and add new ones if you like.

All questions are to be put in **JSON** format as specified earlier:
1. In case you are still using the */sample folder*, in any .json file in *resource/sample/data* or its subfolders.
2. Or if you started using */private* folder, in any .json file in *resources/private/data* or its subfolders.

### 7. Configure your Question sets
```
questions:
  available-categories:
    "[programming]": ["programming"]
    "[philosophy]": ["philosophy"]
```

Default sample configuration creates two quizzes for users to play: *programming* and *philosophy*.
<br> The first column (keys) is visible by the end user as the name of the quiz.
<br> The second column (values) are the tags. All questions loaded from JSON files that include at least one of the tags will be pulled to create the specific quiz set.
<br> In a case like this :
```
"["dogs"]": ["poodles", "terriers", "greyhounds"]
```
a question set *dogs* would be created, which would include all questions loaded from the *data* folder, in which you included either tag: *poodles*, *terriers* or *greyhounds*.

### 8. That's largely it, have fun using the bot!
