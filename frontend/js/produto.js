// produto.js — CRUD simples de produtos (ainda local)

let produtos = [];
let idCounter = 1;

const tabela = document.querySelector('#tabela-produtos tbody');
const adicionarBtn = document.getElementById('adicionar-btn');

adicionarBtn.addEventListener('click', () => {
    const nome = document.getElementById('nome').value;
    const preco = document.getElementById('preco').value;
    const quantidade = document.getElementById('quantidade').value;
    const categoria = document.getElementById('categoria').value;

    if (!nome || !preco || !quantidade) {
        alert('Preencha todos os campos!');
        return;
    }

    const produto = {
        id: idCounter++,
        nome,
        preco: parseFloat(preco),
        quantidade: parseInt(quantidade),
        categoria
    };

    produtos.push(produto);
    atualizarTabela();
    limparCampos();
});

function atualizarTabela() {
    tabela.innerHTML = '';
    produtos.forEach(prod => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${prod.id}</td>
            <td>${prod.nome}</td>
            <td>R$ ${prod.preco.toFixed(2)}</td>
            <td>${prod.quantidade}</td>
            <td>${prod.categoria}</td>
            <td>
                <button onclick="editar(${prod.id})">Editar</button>
                <button onclick="remover(${prod.id})">Remover</button>
            </td>
        `;
        tabela.appendChild(tr);
    });
}

function limparCampos() {
    document.getElementById('nome').value = '';
    document.getElementById('preco').value = '';
    document.getElementById('quantidade').value = '';
}

function editar(id) {
    const prod = produtos.find(p => p.id === id);
    if (prod) {
        document.getElementById('nome').value = prod.nome;
        document.getElementById('preco').value = prod.preco;
        document.getElementById('quantidade').value = prod.quantidade;
        document.getElementById('categoria').value = prod.categoria;
        remover(id);
    }
}

function remover(id) {
    produtos = produtos.filter(p => p.id !== id);
    atualizarTabela();
}
