// main.js — login + alternar seções + carregar lista de produtos

(function () {
  const API = "http://localhost:8080";

  const inputLogin = document.getElementById("login-nome");
  const inputSenha = document.getElementById("login-senha");
  const btnLogin   = document.getElementById("login-btn");
  const msgLogin   = document.getElementById("login-msg");

  const secLogin   = document.getElementById("login-section");
  const secProduto = document.getElementById("produto-section");

  async function fazerLogin() {
    const login = (inputLogin.value || "").trim();
    const senha = (inputSenha.value || "").trim();

    if (!login || !senha) {
      msgLogin.textContent = "⚠️ Preencha usuário e senha.";
      msgLogin.style.color = "orange";
      return;
    }

    try {
      const resp = await fetch(`${API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login, senha })
      });

      if (resp.ok) {
        const usuario = await resp.json();
        msgLogin.textContent = "✅ Bem-vindo, " + (usuario.nome || login) + "!";
        msgLogin.style.color = "green";

        // mostra produtos e esconde login
        secLogin.style.display = "none";
        secProduto.style.display = "block";

        // carrega a tabela ao entrar
        if (window.HS_loadProdutos) window.HS_loadProdutos();
      } else {
        const msg = await resp.json().catch(() => "Usuário ou senha incorretos!");
        msgLogin.textContent = "❌ " + msg;
        msgLogin.style.color = "red";
      }
    } catch (e) {
      console.error(e);
      msgLogin.textContent = "❌ Erro de comunicação com o servidor.";
      msgLogin.style.color = "red";
    }
  }

  if (btnLogin) {
    btnLogin.addEventListener("click", (ev) => {
      ev.preventDefault();
      fazerLogin();
    });
  }
})();
