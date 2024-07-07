# top-scorer-service
Intuit Craft Demo project

## Assumptions 
Topic Simulation: Scores are published using a flat file, POST API, or RabbitMQ.

Database Choice: PostgreSQL is used to persist scores.

Scalability: The system handles a high volume of updates and efficiently retrieves the top 5 scores.

Data Consistency: Ensures top scores are accurate and up-to-date.

Score Validity: Scores are always for registered users, players, and games, and are legitimate without requiring verification.

Player Ranking: Rankings are determined lexicographically by player_id for identical scores, with unique playerId values.

Update Mechanism: Player data is updated individually with no simultaneous updates, as each player plays from one device at a time.

Leaderboard Size: At startup, a fixed number N dictates the leaderboard size, which shows the top N scores with player userIdentifier.

Availability: PostgreSQL and Redis are always accessible.

Internal APIs not requiring data validation, authentication, or authorization.

## Flow 
1. Admins create a game and register it with the Game service.
    * Upon a player completing a game, the Game service publishes the score to our Leaderboard service.
    * Whenever a game is played (First time), a corresponding leaderboard is available.
2. Upon consumption of scores post-game completion, the leaderboard is dynamically updated.
3. After a game has been played at least once, the top N player details can be fetched at any given time.

## Future Improvement

Confluence for LLD:
https://vasusharma.atlassian.net/wiki/spaces/SD/pages/65712/Craft+Demo+TOP+SCORER+TRACKER+-+Low+Level+Design

Data flow diagram:
https://drive.google.com/file/d/1dBjBjDOwpwuPJU-vDfEVU7VUocm1ZHSr/view

Class diagram:
https://lucid.app/lucidchart/c499757c-04e9-4131-abbc-94d4921314b6/view?page=0_0&invitationId=inv_706c9dca-e041-4632-a303-64736a36df2d#
