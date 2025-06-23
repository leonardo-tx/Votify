-- Inicialização do usuário ADMIN para ambiente de produção
-- A cláusula ON DUPLICATE KEY UPDATE garante que o comando não falhará caso o registro já exista
INSERT INTO TB_USER (id, userName, name, email, password, USER_TYPE)
VALUES
    -- Senha admin123
    (1, 'admin', 'Administrator', 'admin@votify.com.br', '$2a$10$iZxot5DeNjNJbm6nHgNjgun1s3NGDZitVB3bsezXntbORUQ2lF5Xi', 'ADMIN')
    ON DUPLICATE KEY UPDATE id = id;