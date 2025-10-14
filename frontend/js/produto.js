// produto.js — CRUD de produtos integrado ao backend (Spark + MySQL)

(function () {
  // 🔧 use sempre o mesmo host do CORS configurado no servidor
  const API = "http://127.0.0.1:8080";

  const tabelaBody = document.querySelector("#tabela-produtos tbody");
  const btnAdd = document.getElementById("adicionar-btn");

  const inputNome = document.getElementById("nome");
  const inputPreco = document.getElementById("preco");
  const inputQtd = document.getElementById("quantidade");
  const selectCat = document.getElementById("categoria");

  // 🔁 converte valores do select (singular) para enum (plural) do backend
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

  // ----------------------------------------------
  // 🔹 Carregar lista de produtos
  // ----------------------------------------------
  async function carregarProdutos() {
    try {
      const resp = await fetch(`${API}/produtos`, {
        method: "GET",
        headers: { "Accept": "application/json" },
      });

      if (!resp.ok) throw new Error(`GET /produtos -> ${resp.status}`);

      const lista = await resp.json();
      console.log("📦 Produtos recebidos:", lista);

      tabelaBody.innerHTML = "";

      if (!Array.isArray(lista)) {
        throw new Error("Resposta inesperada: não é uma lista de produtos");
      }

      lista.forEach((p) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${p.id ?? "-"}</td>
          <td>${p.nome ?? "-"}</td>
          <td>${fmt(p.preco ?? 0)}</td>
          <td>${p.quantidade ?? 0}</td>
          <td>${p.categoria ?? "-"}</td>
          <td class="acoes"></td>
        `;

        const acoes = tr.querySelector(".acoes");

        // Mostra botões somente se for ADMIN
        const usuario = window.HS_getUsuarioAtivo ? window.HS_getUsuarioAtivo() : null;
        if (usuario && usuario.role === "ADMIN") {
          const btnEditar = document.createElement("button");
          btnEditar.textContent = "Editar";
          btnEditar.className = "btn-editar";
          btnEditar.onclick = () => editar(p.id);

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
      alert("❌ Erro ao carregar produtos. Verifique se o servidor está rodando.");
    }
  }

  // ----------------------------------------------
  // 🔹 Adicionar produto
  // ----------------------------------------------
  async function adicionar() {
    const payload = {
      nome: (inputNome.value || "").trim(),
      preco: parseFloat(String(inputPreco.value).replace(",", ".")),
      quantidade: parseInt(inputQtd.value, 10),
      categoria: mapCategoria(selectCat.value),
    };

    if (!payload.nome || Number.isNaN(payload.preco) || Number.isNaN(payload.quantidade)) {
      alert("⚠️ Preencha nome, preço (ex: 2.99) e quantidade (ex: 5).");
      return;
    }

    const usuario = window.HS_getUsuarioAtivo ? window.HS_getUsuarioAtivo() : null;

    try {
      const resp = await fetch(`${API}/produtos`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-User-Role": usuario?.role || "FUNCIONARIO",
        },
        body: JSON.stringify(payload),
      });

      if (resp.status === 201) {
        limparCampos();
        await carregarProdutos();
      } else {
        const msg = await resp.text();
        alert("❌ Erro ao adicionar: " + msg);
      }
    } catch (e) {
      console.error("Erro ao adicionar:", e);
      alert("❌ Erro ao enviar produto ao servidor.");
    }
  }

  // ----------------------------------------------
  // 🔹 Funções auxiliares
  // ----------------------------------------------
  function limparCampos() {
    inputNome.value = "";
    inputPreco.value = "";
    inputQtd.value = "";
    selectCat.value = "FRUTA";
  }

  function editar(id) {
    alert("🛠️ Função de editar produto " + id + " ainda não implementada.");
  }

  async function remover(id) {
    if (!confirm("Tem certeza que deseja remover o produto ID " + id + "?")) return;

    try {
      const resp = await fetch(`${API}/produtos/${id}`, { method: "DELETE" });
      if (resp.status === 204) {
        await carregarProdutos();
      } else {
        alert("❌ Erro ao remover produto ID " + id);
      }
    } catch (e) {
      console.error("Erro ao remover:", e);
      alert("❌ Falha ao remover produto.");
    }
  }

  // ----------------------------------------------
  // 🔹 Inicialização
  // ----------------------------------------------
  if (btnAdd) btnAdd.addEventListener("click", adicionar);

  // Torna acessível globalmente para o main.js
  window.HS_loadProdutos = carregarProdutos;
})();
