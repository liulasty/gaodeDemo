-- OAuth2客户端初始化数据
-- 注意: client_secret为明文'secret'使用{noop}前缀表示不加密

-- 添加测试客户端
INSERT INTO oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_name,
    client_authentication_methods, authorization_grant_types, redirect_uris, scopes, client_settings, token_settings)
VALUES 
(
    'e536f8db-7619-4bb9-beae-8951e8a10d2a', -- id (UUID)
    'client', -- client_id
    CURRENT_TIMESTAMP, -- client_id_issued_at
    '{noop}secret', -- client_secret (明文密码前面加{noop})
    '测试客户端', -- client_name
    'client_secret_basic', -- client_authentication_methods
    'authorization_code,refresh_token,client_credentials', -- authorization_grant_types
    'http://127.0.0.1:8080/login/oauth2/code/client,http://127.0.0.1:8080/authorized', -- redirect_uris
    'openid,profile,read,write', -- scopes
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', -- client_settings (JSON格式)
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":"RS256","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000]}' -- token_settings (JSON格式)
);

-- 添加Web前端客户端
INSERT INTO oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_name,
    client_authentication_methods, authorization_grant_types, redirect_uris, scopes, client_settings, token_settings)
VALUES 
(
    'f171f01d-85f6-42b7-98c2-d97d2d3c9df1', -- id (UUID)
    'web-client', -- client_id
    CURRENT_TIMESTAMP, -- client_id_issued_at
    '{noop}web-secret', -- client_secret (明文密码前面加{noop})
    'Web前端应用', -- client_name
    'client_secret_basic', -- client_authentication_methods
    'authorization_code,refresh_token', -- authorization_grant_types
    'http://localhost:3000/callback,http://localhost:3000/silent-renew.html', -- redirect_uris
    'openid,profile,api.read,api.write', -- scopes
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', -- client_settings (JSON格式)
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":"RS256","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000]}' -- token_settings (JSON格式)
);

-- 添加移动应用客户端
INSERT INTO oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_name,
    client_authentication_methods, authorization_grant_types, redirect_uris, scopes, client_settings, token_settings)
VALUES 
(
    'd80baf08-0eb2-4246-aae1-6f51968d71c7', -- id (UUID)
    'mobile-app', -- client_id
    CURRENT_TIMESTAMP, -- client_id_issued_at
    '{noop}mobile-secret', -- client_secret (明文密码前面加{noop})
    '移动应用', -- client_name
    'client_secret_basic', -- client_authentication_methods
    'authorization_code,refresh_token', -- authorization_grant_types
    'com.example.app:/oauth2/callback', -- redirect_uris (移动应用回调URI)
    'openid,profile,api.read', -- scopes
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}', -- client_settings (JSON格式)
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":"RS256","settings.token.access-token-time-to-live":["java.time.Duration",7200.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",259200.000000000]}' -- token_settings (JSON格式)
); 