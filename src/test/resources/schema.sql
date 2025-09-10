CREATE TABLE IF NOT EXISTS persistent_logins (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);

INSERT INTO user_role_data (role)
SELECT 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM user_role_data);

ALTER TABLE notification
    ADD COLUMN IF NOT EXISTS friendship_id BIGINT
        AS (JSON_UNQUOTE(JSON_EXTRACT(data_payload, '$.fid'))) VIRTUAL;
CREATE INDEX IF NOT EXISTS idx_notification_friendship_id ON notification (friendship_id);

