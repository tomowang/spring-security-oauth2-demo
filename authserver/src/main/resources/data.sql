INSERT INTO oauth_client_details(client_id, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, additional_information, autoapprove) VALUES
('client', '$2a$10$I.tYWXP0kAgBFkXd5nPkgesqbafkaI1k.e1JNR378GNylA5ishux2', 'read',
'authorization_code,client_credentials', 'http://127.0.0.1:9999/login', 'ROLE_CLIENT', '{}', 'true'),
('resource', '$2a$10$I.tYWXP0kAgBFkXd5nPkgesqbafkaI1k.e1JNR378GNylA5ishux2', 'read',
'authorization_code,client_credentials', 'http://127.0.0.1:9090/login', 'ROLE_CLIENT', '{}', 'true');

-- admin / 111111
INSERT INTO users(id, username, password, email, authorities, company_id, region, enabled, verified) VALUES
('00000000-0000-0000-0000-000000000000', 'admin', '$2a$04$NPRmSId0vnpwEIZ5GyF2ou90dJ7L5bf.06TT1WY5Qwzy9vkL.hAg6', 'admin@example.com', 'ROLE_SUPER_ADMIN', '00000000-0000-0000-0000-000000000000', 'all', TRUE, TRUE),
('99999999-9999-9999-9999-999999999999', 'user', '$2a$04$NPRmSId0vnpwEIZ5GyF2ou90dJ7L5bf.06TT1WY5Qwzy9vkL.hAg6', 'user@example.com', 'ROLE_USER', '99999999-9999-9999-9999-999999999999', 'all', TRUE, TRUE);