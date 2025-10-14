// main.js — controle de login, logout e permissões
(function () {
  const API = "http://127.0.0.1:8080";

  const inputLogin = document.getElementById("login-nome");
  const inputSenha = document.getElementById("login-senha");
  const btnLogin = document.getElementById("login-btn");
  const msgLogin = document.getElementById("login-msg");

  const secLogin = document.getElementById("login-section");
  const secProduto = document.getElementById("produto-section");

  const userBar = document.getElementById("userbar");
  const helloName = document.getElementById("hello-name");
  const logoutBtn = document.getElementById("logout-btn");

  let usuarioAtivo = null;

  // 🔹 tenta restaurar sessão salva no navegador
  const usuarioSalvo = localStorage.getItem("usuarioAtivo");
  if (usuarioSalvo) {
    usuarioAtivo = JSON.parse(usuarioSalvo);
    aplicarPermissoes(usuarioAtivo);
    mostrarTelaProdutos();
    if (window.HS_loadProdutos) window.HS_loadProdutos();
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
        body: JSON.stringify({ login, senha }),
      });

      if (resp.ok) {
        const usuario = await resp.json();
        usuarioAtivo = usuario;

        // 🔹 salva no navegador
        localStorage.setItem("usuarioAtivo", JSON.stringify(usuario));

        msgLogin.textContent = "✅ Bem-vindo, " + (usuario.nome || login) + "!";
        msgLogin.style.color = "green";

        mostrarTelaProdutos();
        aplicarPermissoes(usuario);

        if (window.HS_loadProdutos) window.HS_loadProdutos();
      } else {
        const msg = await resp.json().catch(() => ({ mensagem: "Usuário ou senha incorretos!" }));
        msgLogin.textContent = "❌ " + msg.mensagem;
        msgLogin.style.color = "red";
      }
    } catch (e) {
      console.error("Erro ao fazer login:", e);
      msgLogin.textContent = "❌ Erro de comunicação com o servidor.";
      msgLogin.style.color = "red";
    }
  }

  function aplicarPermissoes(usuario) {
    const btnAdd = document.getElementById("adicionar-btn");
    if (!usuario || usuario.role !== "ADMIN") {
      if (btnAdd) btnAdd.style.display = "none";
    } else {
      if (btnAdd) btnAdd.style.display = "inline-block";
    }

    const header = document.querySelector("header h2");
    if (header) {
      header.textContent = `Gerenciamento de Produtos (${usuario.role})`;
    }

    window.HS_getUsuarioAtivo = () => usuarioAtivo;
  }

  function mostrarTelaProdutos() {
    secLogin.style.display = "none";
    secProduto.style.display = "block";
    userBar.style.display = "flex";
    helloName.textContent = usuarioAtivo?.nome || usuarioAtivo?.login || "Usuário";
  }

  function fazerLogout() {
    usuarioAtivo = null;
    localStorage.removeItem("usuarioAtivo");
    secProduto.style.display = "none";
    secLogin.style.display = "block";
    userBar.style.display = "none";
    msgLogin.textContent = "";
    inputLogin.value = "";
    inputSenha.value = "";
  }

  if (btnLogin) btnLogin.addEventListener("click", (ev) => { ev.preventDefault(); fazerLogin(); });
  if (logoutBtn) logoutBtn.addEventListener("click", (ev) => { ev.preventDefault(); fazerLogout(); });
})();
