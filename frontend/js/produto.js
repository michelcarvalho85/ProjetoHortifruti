// produto.js — CRUD de produtos integrado ao backend (Spark + MySQL) — corrigido e robusto
(function () {
  const API = "http://127.0.0.1:8080";

  const tabelaBody = document.querySelector("#tabela-produtos tbody");
  const btnAdd = document.getElementById("adicionar-btn");
  const inputNome = document.getElementById("nome");
  const inputPreco = document.getElementById("preco");
  const inputQtd = document.getElementById("quantidade");
  const selectCat = document.getElementById("categoria");
  let editandoId = null;

  const mapCategoria = (val) => {
    const up = String(val || "").toUpperCase();
    if (up === "FRUTA") return "FRUTAS";
    if (up === "VERDURA") return "VERDURAS";
    if (up === "LEGUME") return "LEGUMES";
    return up;
  };

  const fmt = (n) => {
    const num = typeof n === "number" ? n : parseFloat(String(n).replace(",", "."));
    if (Number.isNaN(num)) return n;
    return "R$ " + num.toFixed(2);
  };

  function getRoleAtual() {
    // ✅ usuário com fallbacks (HS_getUsuarioAtivo -> localStorage -> data-role)
    let usuario = null;
    if (window.HS_getUsuarioAtivo) {
      try { usuario = window.HS_getUsuarioAtivo(); } catch (e) {}
    }
    if (!usuario) {
      try { usuario = JSON.parse(localStorage.getItem("usuarioAtivo") || "null"); } catch (e) {}
    }
    return ((usuario && usuario.role) || document.body.dataset.role || "").toUpperCase();
  }

  async function carregarProdutos() {
    try {
      const resp = await fetch(`${API}/produtos`);
      if (!resp.ok) throw new Error(`GET /produtos -> ${resp.status}`);
      const lista = await resp.json();
      tabelaBody.innerHTML = "";

      const roleAtual = getRoleAtual();

      // mostrar/ocultar botão Adicionar aqui também (reforço local)
      if (btnAdd) btnAdd.style.display = roleAtual === "ADMIN" ? "inline-block" : "none";

      lista.forEach((p) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${p.id}</td>
          <td>${p.nome}</td>
          <td>${fmt(p.preco)}</td>
          <td>${p.quantidade}</td>
          <td>${p.categoria}</td>
          <td class="acoes"></td>
        `;
        const acoes = tr.querySelector(".acoes");

        if (roleAtual === "ADMIN") {
          const btnEditar = document.createElement("button");
          btnEditar.textContent = "Editar";
          btnEditar.className = "btn-editar";
          btnEditar.onclick = () => editar(p);

          const btnRemover = document.createElement("button");
          btnRemover.textContent = "Remover";
          btnRemover.className = "btn-remover";
          btnRemover.onclick = () => remover(p.id);

          acoes.append(btnEditar, btnRemover);
        } else {
          acoes.textContent = "—";
        }

        tabelaBody.appendChild(tr);
      });
    } catch (e) {
      console.error("❌ Erro ao carregar produtos:", e);
    }
  }

  async function salvarProduto() {
    const payload = {
      nome: inputNome.value.trim(),
      preco: parseFloat(String(inputPreco.value).replace(",", ".")),
      quantidade: parseInt(inputQtd.value, 10),
      categoria: mapCategoria(selectCat.value),
    };

    // ✅ inclui papel no header (se existir)
    let role = (document.body.dataset.role || "").toUpperCase();
    if (!role && window.HS_getUsuarioAtivo) {
      try { role = (window.HS_getUsuarioAtivo()?.role || "").toUpperCase(); } catch (_) {}
    }
    if (!role) {
      try { role = (JSON.parse(localStorage.getItem("usuarioAtivo")||"null")?.role || "").toUpperCase(); } catch (_) {}
    }

    const metodo = editandoId ? "PUT" : "POST";
    const url = editandoId ? `${API}/produtos/${editandoId}` : `${API}/produtos`;

    try {
      const resp = await fetch(url, {
        method: metodo,
        headers: {
          "Content-Type": "application/json",
          "X-User-Role": role || "FUNCIONARIO",
        },
        body: JSON.stringify(payload),
      });

      if (resp.ok) {
        limparCampos();
        await carregarProdutos();
        editandoId = null;
        btnAdd.textContent = "Adicionar Produto";
      } else {
        const msg = await resp.text();
        alert("❌ Erro ao salvar produto: " + msg);
      }
    } catch (e) {
      console.error("Erro ao salvar:", e);
      alert("❌ Erro de comunicação com o servidor.");
    }
  }

  function limparCampos() {
    inputNome.value = "";
    inputPreco.value = "";
    inputQtd.value = "";
    selectCat.value = "FRUTA";
    editandoId = null;
  }

  function editar(produto) {
    editandoId = produto.id;
    inputNome.value = produto.nome;
    inputPreco.value = (typeof produto.preco === "number" ? produto.preco : String(produto.preco)).toString().replace(".", ",");
    inputQtd.value = produto.quantidade;

    // tenta inverter para o select (se o back retornar FRUTAS/VERDURAS/LEGUMES)
    const raw = String(produto.categoria || "").toUpperCase();
    if (raw.startsWith("FRUT")) selectCat.value = "FRUTA";
    else if (raw.startsWith("VERD")) selectCat.value = "VERDURA";
    else if (raw.startsWith("LEG")) selectCat.value = "LEGUME";
    else selectCat.value = "FRUTA";

    btnAdd.textContent = "Salvar Alterações";
  }

  async function remover(id) {
    if (!confirm("Tem certeza que deseja remover o produto ID " + id + "?")) return;
    try {
      let role = (document.body.dataset.role || "").toUpperCase();
      if (!role && window.HS_getUsuarioAtivo) {
        try { role = (window.HS_getUsuarioAtivo()?.role || "").toUpperCase(); } catch (_) {}
      }
      const resp = await fetch(`${API}/produtos/${id}`, {
        method: "DELETE",
        headers: { "X-User-Role": role || "FUNCIONARIO" }
      });
      if (!resp.ok) throw new Error(`DELETE /produtos/${id} -> ${resp.status}`);
      await carregarProdutos();
    } catch (e) {
      console.error("❌ Erro ao remover:", e);
      alert("❌ Erro de comunicação ao remover o produto.");
    }
  }

  if (btnAdd) btnAdd.addEventListener("click", salvarProduto);

  // disponibiliza para o restante do app
  window.HS_loadProdutos = carregarProdutos;

  // 🔔 quando login concluir, recarrega a tabela (garante botões ADMIN)
  window.addEventListener("hs-login-concluido", () => {
    setTimeout(() => carregarProdutos(), 300);
  });

  // 🚀 boot: se a página abrir com sessão já salva, renderiza tabela mesmo sem evento
  setTimeout(() => {
    try { carregarProdutos(); } catch (e) {}
  }, 120);
})();
