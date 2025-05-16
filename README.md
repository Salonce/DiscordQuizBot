# 🎯 Discord A B C D Quiz Bot

A Discord quiz bot built in **Java** using **Discord4J** and **reactive streams**, designed primarily for **learning** through **A, B, C, D multiple-choice questions** — you're free to add any kind of topics you want.

---

## ✨ Features

- ✅ Supports **A, B, C, D multiple-choice quizzes**
- 🕒 **30-second timer** per question
- 🔁 **10-question rounds**, asked back-to-back
- 💬 One quiz **per channel** at a time
- 🎮 Works in **both channels and DMs**
- 👥 **Single-player or multiplayer** competitive support
- 🧠 **Dynamic countdowns** and answer tracking for smooth UX
- 📊 **Final scoreboard** showing all players and their performance

## 🛠 Getting Started

### 1. Clone the Repository

```
git clone https://github.com/yourusername/discord-quiz-bot.git
cd discord-quiz-bot
```

### 2. Setup Environment

- Copy the example environment file *.env.example* (but name it just *.env*):

```
cp .env.example .env
```
- Open *.env* and fill in your Discord bot token.

```
# .env
DISCORD_TOKEN=your_bot_token_here
```

### 3. Add questions (JSON files)

All questions are to be put in **JSON**

in any file in *resources/private/data* or its subfolders <br>
<br> OR <br>

in any file in *resource/sample/data* or its subfolders <br>



Questions are sorted into levels by difficulty. The higher the difficulty, the higher the level at which question will appear.
Set difficulty to 1 for the easiest questions and 100 (or another number of your choice) for the hardest ones (difficulty level can repeat).


```
{
"id": 1000000,
"question": "Question?",
"correctAnswers": ["Correct answer"],
"incorrectAnswers": ["Incorrect answer 1", "Incorrect answer 2", "Incorrect answer 3"],
"explanation": "Explanation regarding the answer that shows up with the correct answer."
"difficulty": 1
"tags": ["brain", "neuroscience", "human memory"],
}
```


### 4. Configure Your Question sets
By default, the bot looks for quiz configuration in:

```
src/main/resources/private/application.yml
```
If the above file doesn't exist, it falls back to:
```
src/main/resources/sample/application-sample.yml
```

Option A: Use Your Own Private Quiz Config
Create a folder:

```
src/main/resources/private/
```

Add a file named:
*application.yml*

And fill it with configuration analogical to the one in the application-sample.yml file.

```
questions:
  available-topics:
    programming: ["programming"]
    philosophy: ["philosophy"]
    
# This part means: create two selectable quizzes: programming and philosophy (keys), and put in them all questions including tags "programming" and "philosophy" (values).
# It's possible to put more than one tag in the array. Then the topic will include more than these questions.
```




![starting](https://i.imgur.com/qbvJKU6.gif)
![middle_game](https://i.imgur.com/gYcZ0EN.gif)