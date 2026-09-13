-- Promove somente o usuario academico de demonstracao para o perfil ADMIN.
-- CPF de demonstracao: 52998224725.

INSERT INTO auth_usuario_perfil (usuario_id, perfil_id)
SELECT usuario.id, perfil.id
  FROM auth_usuario usuario
  JOIN auth_perfil perfil ON perfil.nome = 'ADMIN'
 WHERE usuario.cpf = '52998224725'
ON CONFLICT (usuario_id, perfil_id) DO NOTHING;

DELETE FROM auth_usuario_perfil usuario_perfil
 USING auth_usuario usuario, auth_perfil perfil
 WHERE usuario_perfil.usuario_id = usuario.id
   AND usuario_perfil.perfil_id = perfil.id
   AND usuario.cpf = '52998224725'
   AND perfil.nome = 'CUSTOMER';
