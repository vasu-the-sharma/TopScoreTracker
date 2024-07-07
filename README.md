# TopScoreTracker
An Intuit Craft Demo project.


## Tech Stack
Language: Java
Framework: Springboot
Database: PostgreSQL
Caching: Redis
Message Broker: RabbitMQ
Runtime Environment: Docker
Hosting: Github


## Assumptions 
1. Topic Simulation: Scores are published using a flat file, POST API, or RabbitMQ.
2. Database Choice: PostgreSQL is used to persist scores.
3. Scalability: The system handles a high volume of updates and efficiently retrieves the top 5 scores.
4. Data Consistency: Ensures top scores are accurate and up-to-date.
5. Score Validity: Scores are always for registered users, players, and games, and are legitimate without requiring verification.
6. Player Ranking: Rankings are determined lexicographically by player_id for identical scores, with unique playerId values.
7. Update Mechanism: Player data is updated individually with no simultaneous updates, as each player plays from one device at a time.
8. Leaderboard Size: At startup, a fixed number N dictates the leaderboard size, which shows the top N scores with player userIdentifier.
9. Availability: PostgreSQL and Redis are always accessible.
10. Internal APIs not requiring data validation, authentication, or authorization.


## Data Flow 
1. Admins create a game and register it with the Game service -> Assumption
    * Upon a player completing a game, the Game service publishes the score to our Leaderboard service.
    * Whenever a game is played, a corresponding leaderboard is available.
2. Upon addition of new scores, the leaderboard is dynamically updated.
3. After a game has been played, at least once, the top N player details can be fetched at any given time.
4. You can ingest the score via three ways:
   A. RabbitMQ
   B. Post API
   C. Flat file ingestion


## Future Enhancements
Real-time Updates: Implement WebSocket or Server-Sent Events (SSE) to push real-time updates to clients whenever the leaderboard changes.

Global and Local Leaderboards: Introduce leaderboards based on different criteria such as geographic location, age group, or game level.

Historical Data and Analytics: Store historical data to allow players to view their progress over time and provide analytics on player performance.

User Profiles and Achievements: Extend the player data to include profiles and achievements, enhancing player engagement.

Game Variants: Support multiple game variants with separate leaderboards for each variant.

Security Enhancements: Implement authentication and authorization for APIs to ensure only legitimate data sources can publish scores.

Enhanced Data Validation: Introduce more robust data validation and sanitization processes to ensure the integrity of the scores being ingested.

Multi-Language Support: Add support for multiple languages to make the service more accessible to a global audience.


## Scalability
Database Sharding: Implement sharding for PostgreSQL to distribute the load across multiple database instances, ensuring the system can handle a large number of concurrent writes and reads.

Horizontal Scaling: Use container orchestration tools like Kubernetes to deploy and manage multiple instances of the service, ensuring high availability and load balancing.

Caching: Optimize the use of Redis for caching not just the top scores but also frequently accessed data like player profiles and game information.

Asynchronous Processing: Use message queues (RabbitMQ) to handle score ingestion asynchronously, decoupling the ingestion process from the processing and ensuring the system can handle bursts of incoming data.

Load Balancing: Deploy load balancers to distribute incoming requests evenly across multiple instances of the service.


## Points of Failure
Single Points of Failure:
   1. Redis: If Redis goes down, the system's ability to quickly retrieve top scores will be impacted. Mitigate by using Redis clusters for high availability.
   2. PostgreSQL: Database failure can lead to data loss or unavailability. Use replication and failover strategies to ensure high availability.

Network Latency: High network latency can affect the performance of the service, especially if the components (database, message broker, and caching layer) are geographically distributed.

Message Broker Overload: RabbitMQ can become a bottleneck if it gets overloaded with messages. Implement rate limiting and ensure RabbitMQ is properly scaled.

Concurrency Issues: Handling concurrent updates to the same player's score can lead to race conditions. Use transactions and locks to ensure data consistency.

Service Downtime: If any service instance goes down, it can affect the overall system performance. Ensure proper monitoring and auto-scaling to replace failed instances quickly.

Data Consistency: Inconsistencies between Redis cache and PostgreSQL can lead to incorrect leaderboard data. Implement cache invalidation strategies and periodic synchronization.


## Links & Diagrams

1. Confluence for LLD:
https://vasusharma.atlassian.net/wiki/spaces/SD/pages/65712/Craft+Demo+TOP+SCORER+TRACKER+-+Low+Level+Design

2. Data flow diagram:
https://drive.google.com/file/d/1dBjBjDOwpwuPJU-vDfEVU7VUocm1ZHSr/view

3. Class diagram:
https://lucid.app/lucidchart/c499757c-04e9-4131-abbc-94d4921314b6/view?page=0_0&invitationId=inv_706c9dca-e041-4632-a303-64736a36df2d#
