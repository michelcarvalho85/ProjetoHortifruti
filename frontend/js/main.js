document.addEventListener("DOMContentLoaded", () => {
  const loginBtn = document.getElementById("login-btn");
  const loginMsg = document.getElementById("login-msg");

  if (loginBtn) {
    loginBtn.addEventListener("click", async (event) => {
      event.preventDefault();

      // Pega os valores digitados nos campos
      const login = document.getElementById("login-nome").value.trim();
      const senha = document.getElementById("login-senha").value.trim();

      if (!login || !senha) {
        loginMsg.textContent = "⚠️ Preencha usuário e senha.";
        loginMsg.style.color = "orange";
        return;
      }

      // Monta o JSON que o backend espera
      const dados = { login, senha };

      try {
        const resposta = await fetch("http://localhost:8080/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(dados)
        });

        if (resposta.ok) {
          const usuario = await resposta.json();

          loginMsg.textContent = "✅ Bem-vindo, " + usuario.nome + "!";
          loginMsg.style.color = "green";

          // Exibe a área de produtos e esconde o login
          document.getElementById("login-section").style.display = "none";
          document.getElementById("produto-section").style.display = "block";

        } else {
          const msg = await resposta.json();
          loginMsg.textContent = "❌ " + msg;
          loginMsg.style.color = "red";
        }
      } catch (erro) {
        console.error("Erro:", erro);
        loginMsg.textContent = "❌ Erro de conexão com o servidor.";
        loginMsg.style.color = "red";
      }
    });
  }
});
