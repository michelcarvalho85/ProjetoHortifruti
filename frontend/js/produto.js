// produto.js — CRUD de produtos integrado ao backend (Spark + MySQL)

(function () {
  const API = "http://localhost:8080";

  const tabelaBody = document.querySelector("#tabela-produtos tbody");
  const btnAdd     = document.getElementById("adicionar-btn");

  const inputNome  = document.getElementById("nome");
  const inputPreco = document.getElementById("preco");
  const inputQtd   = document.getElementById("quantidade");
  const selectCat  = document.getElementById("categoria");

  // converte valores do select (singular) para enum (plural) do backend
  const mapCategoria = (val) => {
    const up = String(val || "").toUpperCase();
    if (up === "FRUTA")   return "FRUTAS";
    if (up === "VERDURA") return "VERDURAS";
    if (up === "LEGUME")  return "LEGUMES";
    return up; // caso já venha em plural
  };

  const fmt = (n) => {
    const num = typeof n === "number" ? n : parseFloat(String(n).replace(",", "."));
    if (Number.isNaN(num)) return n;
    return "R$ " + num.toFixed(2);
  };

  async function carregarProdutos() {
    try {
      const resp = await fetch(`${API}/produtos`);
      if (!resp.ok) throw new Error(`GET /produtos -> ${resp.status}`);
      const lista = await resp.json();

      tabelaBody.innerHTML = "";
      lista.forEach(p => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${p.id}</td>
          <td>${p.nome}</td>
          <td>${fmt(p.preco)}</td>
          <td>${p.quantidade}</td>
          <td>${p.categoria}</td>
          <td>
            <button class="btn-editar"  data-id="${p.id}">Editar</button>
            <button class="btn-remover" data-id="${p.id}">Remover</button>
          </td>
        `;
        tabelaBody.appendChild(tr);
      });

      // ações
      document.querySelectorAll(".btn-remover").forEach(btn => {
        btn.onclick = () => remover(parseInt(btn.dataset.id, 10));
      });

      document.querySelectorAll(".btn-editar").forEach(btn => {
        btn.onclick = () => editar(parseInt(btn.dataset.id, 10));
      });
    } catch (e) {
      console.error("Erro ao carregar produtos:", e);
      alert("❌ Erro ao carregar produtos. Verifique se o servidor está rodando.");
    }
  }

  async function adicionar() {
    const payload = {
      // id omitido -> backend gera
      nome: (inputNome.value || "").trim(),
      preco: parseFloat(String(inputPreco.value).replace(",", ".")),
      quantidade: parseInt(inputQtd.value, 10),
      categoria: mapCategoria(selectCat.value)
    };

    if (!payload.nome || Number.isNaN(payload.preco) || Number.isNaN(payload.quantidade)) {
      alert("⚠️ Preencha nome, preço (ex: 2.99) e quantidade (ex: 5).");
      return;
    }

    try {
      const resp = await fetch(`${API}/produtos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
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

  async function editar(id) {
    const nome = prompt("Novo nome:");
    if (nome === null) return;

    const precoStr = prompt("Novo preço (ex: 3.50):");
    if (precoStr === null) return;
    const preco = parseFloat(String(precoStr).replace(",", "."));

    const qtdStr = prompt("Nova quantidade (ex: 4):");
    if (qtdStr === null) return;
    const quantidade = parseInt(qtdStr, 10);

    const catStr = prompt("Nova categoria (FRUTAS, VERDURAS, LEGUMES, BEBIDAS, OUTROS):");
    if (catStr === null) return;
    const categoria = mapCategoria(catStr);

    const payload = { nome, preco, quantidade, categoria };
    if (!payload.nome || Number.isNaN(preco) || Number.isNaN(quantidade)) {
      alert("⚠️ Dados inválidos.");
      return;
    }

    try {
      const resp = await fetch(`${API}/produtos/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (resp.ok) {
        await carregarProdutos();
      } else {
        const msg = await resp.text();
        alert("❌ Não foi possível atualizar: " + msg);
      }
    } catch (e) {
      console.error("Erro ao atualizar:", e);
      alert("❌ Erro ao atualizar produto no servidor.");
    }
  }

  async function remover(id) {
    const ok = confirm(`Remover produto ${id}?`);
    if (!ok) return;

    try {
      const resp = await fetch(`${API}/produtos/${id}`, { method: "DELETE" });
      if (resp.status === 204) {
        await carregarProdutos();
      } else {
        const msg = await resp.text();
        alert("❌ Não foi possível remover: " + msg);
      }
    } catch (e) {
      console.error("Erro ao remover:", e);
      alert("❌ Erro ao remover produto no servidor.");
    }
  }

  function limparCampos() {
    inputNome.value = "";
    inputPreco.value = "";
    inputQtd.value = "";
    selectCat.value = "FRUTA";
  }

  // liga o clique do botão
  if (btnAdd) {
    btnAdd.addEventListener("click", (ev) => {
      ev.preventDefault();
      adicionar();
    });
  }

  // expõe para o main.js chamar após login
  window.HS_loadProdutos = carregarProdutos;

  // se por algum motivo a seção já estiver visível, carregue
  const prodSection = document.getElementById("produto-section");
  if (!prodSection || prodSection.style.display !== "none") {
    carregarProdutos();
  }
})();
