-- Criar tabela de usuários
CREATE TABLE IF NOT EXISTS TB_USER (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    userName VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    USER_TYPE VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- Criar tabela de pesquisas
CREATE TABLE IF NOT EXISTS TB_POLL (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    creator_id BIGINT NOT NULL,
    poll_status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_responsible_poll FOREIGN KEY (creator_id) REFERENCES TB_USER(id)
) ENGINE=InnoDB;

-- Criar tabela de opções de pesquisa
CREATE TABLE IF NOT EXISTS TB_POLL_OPTION (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    text VARCHAR(255) NOT NULL,
    poll_id BIGINT NOT NULL,
    CONSTRAINT fk_poll_option FOREIGN KEY (poll_id) REFERENCES TB_POLL(id)
) ENGINE=InnoDB;

-- Criar tabela de votos
CREATE TABLE IF NOT EXISTS TB_VOTE (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    poll_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    vote_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_poll_vote FOREIGN KEY (poll_id) REFERENCES TB_POLL(id),
    CONSTRAINT fk_user_vote FOREIGN KEY (user_id) REFERENCES TB_USER(id),
    CONSTRAINT fk_option_vote FOREIGN KEY (option_id) REFERENCES TB_POLL_OPTION(id),
    CONSTRAINT uk_user_poll UNIQUE (user_id, poll_id)
) ENGINE=InnoDB; 