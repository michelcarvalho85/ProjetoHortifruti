# Requisitos Não Funcionais

| Código | Tipo           | Descrição                                                                                                                                         | RF Associados                          |
|--------|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| RNF001 | Usabilidade    | O sistema deve possuir uma interface amigável e intuitiva para facilitar o cadastro e gerenciamento de usuários.                                  | RF001, RF004, RF005                     |
| RNF002 | Desempenho     | O processo de autenticação deve ser concluído em até 2 segundos.                                                                                  | RF002                                   |
| RNF003 | Segurança      | As senhas dos usuários devem ser armazenadas de forma criptografada usando algoritmo seguro (ex: bcrypt).                                         | RF001, RF002, RF003                     |
| RNF004 | Segurança      | O sistema deve bloquear o acesso após 5 tentativas de login malsucedidas consecutivas.                                                            | RF002                                   |
| RNF005 | Usabilidade    | O sistema deve fornecer mensagens claras e orientações durante o processo de alteração de senha.                                                  | RF003                                   |
| RNF006 | Confiabilidade | Todas as operações de manutenção de dados devem ter confirmação de sucesso e exibir mensagens em caso de erro.                                   | RF004, RF005, RF007, RF013              |
| RNF007 | Disponibilidade| O sistema deve estar disponível 99% do tempo em horário comercial.                                                                                | RF006, RF021                            |
| RNF008 | Eficiência     | A listagem de produtos deve retornar resultados em até 3 segundos, mesmo com mais de 1000 itens cadastrados.                                     | RF006                                   |
| RNF009 | Segurança      | Apenas usuários autenticados com perfil adequado poderão acessar funcionalidades de cadastro, edição e exclusão.                                 | RF001, RF026, RF027                      |
| RNF010 | Integridade    | O sistema não deve permitir a finalização de uma compra com dados obrigatórios ausentes.                                                         | RF008, RF009                            |
| RNF011 | Confiabilidade | O estoque deve ser atualizado corretamente em tempo real após finalização de compras e vendas.
