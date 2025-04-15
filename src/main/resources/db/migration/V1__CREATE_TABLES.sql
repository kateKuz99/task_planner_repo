CREATE TABLE users (
    id SERIAL PRIMARY KEY ,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(2048) NOT NULL ,
    email VARCHAR(256) NOT NULL ,
    role VARCHAR(32) NOT NULL ,
    first_name VARCHAR(64)  NOT NULL,
    last_name VARCHAR(64) NOT NULL,
    activation_code VARCHAR(64),
    enabled BOOLEAN NOT NULL DEFAULT FALSE ,
    created_at TIMESTAMP ,
    updated_at TIMESTAMP
);

CREATE TABLE workspaces(
    id SERIAL PRIMARY KEY ,
    name VARCHAR(64)
);

CREATE TABLE projects(
    id SERIAL PRIMARY KEY,
    workspace_id BIGINT UNSIGNED,
    user_id BIGINT UNSIGNED,
    project_role VARCHAR(32),
    FOREIGN KEY(workspace_id) REFERENCES workspaces(id),
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE teams(
    id SERIAL PRIMARY KEY ,
    name VARCHAR(64)
);

CREATE TABLE team_user(
    team_id BIGINT UNSIGNED,
    user_id BIGINT UNSIGNED,
    workspace_id BIGINT UNSIGNED,
    PRIMARY KEY(team_id,user_id),
    FOREIGN KEY(team_id) REFERENCES teams(id),
    FOREIGN KEY(workspace_id) REFERENCES workspaces(id),
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE boards(
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
    workspace_id BIGINT UNSIGNED,
    FOREIGN KEY(workspace_id) REFERENCES workspaces(id)
);

CREATE TABLE zones(
    id SERIAL PRIMARY KEY,
    board_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(32) NOT NULL,
    task_status VARCHAR(32) NOT NULL,
    FOREIGN KEY(board_id) REFERENCES boards(id)
);
CREATE TABLE tasks(
    id SERIAL PRIMARY KEY,
    description VARCHAR(256) NOT NULL,
    severity VARcHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deadline_at TIMESTAMP NOT NULL,
    zone_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY(zone_id) REFERENCES zones(id)
);

CREATE TABLE workers(
    project_id BIGINT UNSIGNED,
    task_id BIGINT UNSIGNED,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);


