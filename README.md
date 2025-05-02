# Discord A B C D Quiz Bot

Discord quiz bot, A, B, C, D.
Primarily for learning, but add any sort of questions and quizzes you want.

Written in Java + Discord4j with reactive streams.

1. A, B, C, D questions.
2. In standard version it features 10 questions asked back-to-back with a 30 second timer for an answer.
3. One quiz per channel. Works also in DMs.
4. Multiple players playing at the same time and competing, or a single player.
5. Dynamic countdowns, tracking of users and answers that provide fluent and comfortable experience.
6. Final scoreboard shows the winners.



To use the bot, just clone it and make a copy of .env.example and name it just .env.

There is a small standard sample of questions in the /resources/sample folder.
It is advised to create a new folder /resources/private, construed analogically to /resources/sample, and use a file named application.yml in it, instead of application-sample.yml as in /sample instead.
It is also possible to just modify the data in the /sample/ folder, but by default the program will look for /resources/private configuration and if they exist they will override the sample.

![somegifname](https://i.imgur.com/weKHW6Z.gif)