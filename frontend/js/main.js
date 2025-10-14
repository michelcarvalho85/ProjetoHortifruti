// main.js — login + alternar seções + manter sessão ativa

(function () {
  const API = "http://localhost:8080";

  const inputLogin = document.getElementById("login-nome");
  const inputSenha = document.getElementById("login-senha");
  const btnLogin   = document.getElementById("login-btn");
  const msgLogin   = document.getElementById("login-msg");

  const secLogin   = document.getElementById("login-section");
  const secProduto = document.getElementById("produto-section");

  // Função para salvar usuário logado
  function salvarSessao(usuario) {
    sessionStorage.setItem("usuarioLogado", JSON.stringify(usuario));
  }

  // Função para recuperar usuário logado
  function obterSessao() {
    const u = sessionStorage.getItem("usuarioLogado");
    return u ? JSON.parse(u) : null;
  }

  // Função para sair (logout)
  function sair() {
    sessionStorage.removeItem("usuarioLogado");
    secProduto.style.display = "none";
    secLogin.style.display = "block";
  }

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
        salvarSessao(usuario); // mantém logado
        msgLogin.textContent = "✅ Bem-vindo, " + (usuario.nome || login) + "!";
        msgLogin.style.color = "green";

        // Mostra produtos e esconde login
        secLogin.style.display = "none";
        secProduto.style.display = "block";

        if (window.HS_loadProdutos) window.HS_loadProdutos();
      } else {
        const msg = await resp.text();
        msgLogin.textContent = "❌ " + msg;
        msgLogin.style.color = "red";
      }
    } catch (e) {
      console.error(e);
      msgLogin.textContent = "❌ Erro de comunicação com o servidor.";
      msgLogin.style.color = "red";
    }
  }

  // Ligar evento do botão de login
  if (btnLogin) {
    btnLogin.addEventListener("click", (ev) => {
      ev.preventDefault(); // impede o recarregamento da página
      fazerLogin();
    });
  }

  // Verifica se já tem sessão salva
  const usuario = obterSessao();
  if (usuario) {
    secLogin.style.display = "none";
    secProduto.style.display = "block";
    if (window.HS_loadProdutos) window.HS_loadProdutos();
  } else {
    secLogin.style.display = "block";
    secProduto.style.display = "none";
  }

  // Opcional: botão de sair (se tiver no HTML)
  const btnLogout = document.getElementById("logout-btn");
  if (btnLogout) {
    btnLogout.addEventListener("click", sair);
  }
})();
