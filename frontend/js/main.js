// main.js — controle de login, logout e permissões (corrigido e aprimorado)
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

  // 🔹 tenta restaurar sessão salva
  const usuarioSalvo = localStorage.getItem("usuarioAtivo");
  if (usuarioSalvo) {
    try {
      usuarioAtivo = JSON.parse(usuarioSalvo);
      // ✅ garante role disponível no DOM como fallback
      document.body.dataset.role = (usuarioAtivo.role || "").toUpperCase();
      aplicarPermissoes(usuarioAtivo);
      mostrarTelaProdutos();
      if (window.HS_loadProdutos) window.HS_loadProdutos();
    } catch (_) {}
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
      // evita clique duplo
      if (btnLogin) btnLogin.disabled = true;

      const resp = await fetch(`${API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login, senha }),
      });

      if (resp.ok) {
        const usuario = await resp.json();
        usuario.role = (usuario.role || "").toUpperCase(); // normaliza role
        usuarioAtivo = usuario;

        localStorage.setItem("usuarioAtivo", JSON.stringify(usuario));
        // ✅ fallback extra para produto.js
        document.body.dataset.role = usuario.role;

        msgLogin.textContent = "✅ Bem-vindo, " + (usuario.nome || login) + "!";
        msgLogin.style.color = "green";

        aplicarPermissoes(usuario);
        mostrarTelaProdutos();

        // 🔔 avisa o resto do sistema (ex: produto.js) que o login foi concluído
        window.dispatchEvent(new Event("hs-login-concluido"));
        if (window.HS_loadProdutos) setTimeout(() => window.HS_loadProdutos(), 150);
      } else {
        const msg = await resp.json().catch(() => ({ mensagem: "Usuário ou senha incorretos!" }));
        msgLogin.textContent = "❌ " + (msg.mensagem || "Usuário ou senha incorretos!");
        msgLogin.style.color = "red";
      }
    } catch (e) {
      console.error("Erro ao fazer login:", e);
      msgLogin.textContent = "❌ Erro de comunicação com o servidor.";
      msgLogin.style.color = "red";
    } finally {
      if (btnLogin) btnLogin.disabled = false;
    }
  }

  function aplicarPermissoes(usuario) {
    const btnAdd = document.getElementById("adicionar-btn");
    const role = (usuario && usuario.role) ? String(usuario.role).toUpperCase() : "";
    if (!usuario || role !== "ADMIN") {
      if (btnAdd) btnAdd.style.display = "none";
    } else {
      if (btnAdd) btnAdd.style.display = "inline-block";
    }

    const header = document.querySelector("header h2");
    if (header) header.textContent = `Gerenciamento de Produtos (${role || "SEM PERFIL"})`;

    // mantém exportação global acessível
    window.HS_getUsuarioAtivo = () => usuarioAtivo;
  }

  function mostrarTelaProdutos() {
    secLogin.style.display = "none";
    secProduto.style.display = "block";
    userBar.style.display = "flex";
    helloName.textContent = (usuarioAtivo && (usuarioAtivo.nome || usuarioAtivo.login)) || "Usuário";
  }

  function fazerLogout() {
    usuarioAtivo = null;
    localStorage.removeItem("usuarioAtivo");
    delete document.body.dataset.role;
    secProduto.style.display = "none";
    secLogin.style.display = "block";
    userBar.style.display = "none";
    msgLogin.textContent = "";
    inputLogin.value = "";
    inputSenha.value = "";
  }

  // Eventos
  if (btnLogin) btnLogin.addEventListener("click", (ev) => { ev.preventDefault(); fazerLogin(); });
  if (logoutBtn) logoutBtn.addEventListener("click", (ev) => { ev.preventDefault(); fazerLogout(); });

  // Enter para logar
  [inputLogin, inputSenha].forEach(el => {
    if (el) el.addEventListener("keydown", (e) => {
      if (e.key === "Enter") { e.preventDefault(); fazerLogin(); }
    });
  });

  // 🔧 exportação global acessível (definição inicial)
  window.HS_getUsuarioAtivo = () => usuarioAtivo;
})();
