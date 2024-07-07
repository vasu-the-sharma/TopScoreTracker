CREATE TABLE IF NOT EXISTS player_scores (
     id SERIAL PRIMARY KEY,
     player_id VARCHAR(255),
     game_id VARCHAR(255),
     score INT NOT NULL,
     date_created TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
     date_updated TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
     UNIQUE (player_id, game_id),
     CONSTRAINT score_idx
         UNIQUE (score)
) WITH (OIDS=FALSE);

