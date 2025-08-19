
const API = '/api';
const width = 10;

const gamesBoardContainer = document.getElementById('gamesboard-container');
const optionContainer      = document.querySelector('.option-container');
const flipButton           = document.getElementById('flip-button');
const clearBoard           = document.getElementById('clear-board');
const startButton          = document.getElementById('start-button');
const infoDisplay          = document.getElementById('battle-info');
const turnDisplay          = document.getElementById('turn-display');

let angle = 0;
let draggedShip = null;
let playerShips = [];
let gameStarted = false;

// 1) Flip dos previews
flipButton.addEventListener('click', () => {
    angle = angle === 0 ? 90 : 0;
    const shipPreviews = document.querySelectorAll('.ship-preview');
    
    shipPreviews.forEach(ship => {
        ship.style.transform = `rotate(${angle}deg)`;
    });
    
    showNotification(`Navios rotacionados para ${angle === 0 ? 'horizontal' : 'vertical'}`, 'info');
});

// 2) Função que **cria** o board no DOM
function createBoard(color, user) {

    // Cria o contêiner para o tabuleiro e o título
    const container = document.createElement('div');
    container.classList.add('board-container');

    // Cria o título com base no ID do usuário
    const title = document.createElement('h3');
    title.style.color = '#ffffff';
    title.textAlign = 'center';
    if (user === 'player') {
        title.textContent = 'Seu Tabuleiro';
    } else if (user === 'computer') {
        title.textContent = 'Tabuleiro Inimigo';
        container.classList.add('board-container-computer');
    }

    // Cria o board
    const board = document.createElement('div');
    board.classList.add('game-board');
    board.id = user;
    board.style.border = `1px solid ${color}`;
    for (let i = 0; i < width * width; i++) {
        const cell = document.createElement('div');
        cell.classList.add('block', 'water');
        cell.id = i;
        board.append(cell);
    }

    // Anexa o título e o board ao contêiner, e o contêiner ao gamesBoardContainer
    container.appendChild(title);
    container.appendChild(board);
    gamesBoardContainer.append(container);
}

// 3) Inicializa jogo no back-end e recria os boards
async function initGame() {
    await fetch(`${API}/init?size=${width}`);
    gamesBoardContainer.innerHTML = '';
    createBoard('white', 'player');
    createBoard('black',   'computer');
    infoDisplay.textContent = 'Posicione seus navios e clique em START';
    turnDisplay.textContent = '';
    attachPlayerDragHandlers();
}
initGame();

// 4) Drag & Drop dos navios do jogador
function attachPlayerDragHandlers() {
    Array.from(optionContainer.children)
        .forEach(el => el.addEventListener('dragstart', e => draggedShip = e.target));
    document.querySelectorAll('#player .block').forEach(cell => {
        cell.addEventListener('dragover', e => { // Atualiza preview dinamicamente
            e.preventDefault();
            if (draggedShip) {
                showShipPreview(e.target);
            }
        });
        cell.addEventListener('drop', dropShip);
        cell.addEventListener('dragenter', e => { // Mantém para iniciar o preview
            e.preventDefault();
            if (draggedShip) {
                showShipPreview(e.target);
            }
        });
        cell.addEventListener('dragleave', clearPreview); // Limpa ao sair
    });
}

// Função de validação para posicionamento 
function canPlaceShip(startIndex, length, isVertical) {
    const row = Math.floor(startIndex / width);
    const col = startIndex % width;
    
    // Verificar limites do tabuleiro
    if (isVertical) {
        if (row + length > width) return false;
    } else {
        if (col + length > width) return false;
    }
    
    // Verificar sobreposição e adjacência
    for (let i = 0; i < length; i++) {
        let checkIndex;
        if (isVertical) {
            checkIndex = startIndex + (i * width);
        } else {
            checkIndex = startIndex + i;
        }
        
        // Verificar se a célula está ocupada
        const cell = document.querySelector(`#player .block[id='${checkIndex}']`);
        if (cell && cell.classList.contains('taken')) {
            return false;
        }
        
        // Verificar adjacências (células vizinhas)
        if (!isAdjacentCellFree(checkIndex)) {
            return false;
        }
    }
    
    return true;
}

// Função auxiliar para verificar se células adjacentes estão livres
function isAdjacentCellFree(cellIndex) {
    const row = Math.floor(cellIndex / width);
    const col = cellIndex % width;
    
    // Verificar todas as 8 direções adjacentes
    const directions = [
        [-1, -1], [-1, 0], [-1, 1],  // linha superior
        [0, -1],         [0, 1],   // lados
        [1, -1],  [1, 0],  [1, 1]    // linha inferior
    ];
    
    for (let [dRow, dCol] of directions) {
        const newRow = row + dRow;
        const newCol = col + dCol;
        
        // Verificar se está dentro dos limites
        if (newRow >= 0 && newRow < width && newCol >= 0 && newCol < width) {
            const adjacentIndex = newRow * width + newCol;
            const adjacentCell = document.querySelector(`#player .block[id='${adjacentIndex}']`);
            
            if (adjacentCell && adjacentCell.classList.contains('taken')) {
                return false;
            }
        }
    }
    
    return true;
}

//Mostra o preview visual durante o drag
function showShipPreview(targetCell) {
    clearPreview();
    
    if (!draggedShip) return;
    
    const cellIndex = Number(targetCell.id);
    const ship = ships[draggedShip.id];
    const isVertical = angle !== 0;
    
    const isValid = canPlaceShip(cellIndex, ship.length, isVertical);
    const previewClass = isValid ? 'ship-preview-valid' : 'invalid-drop';
    
    for (let i = 0; i < ship.length; i++) {
        let previewIndex;
        if (isVertical) {
            previewIndex = cellIndex + (i * width);
        } else {
            previewIndex = cellIndex + i;
        }
        
        if (previewIndex >= 0 && previewIndex < width * width) {
            const previewCell = document.querySelector(`#player .block[id='${previewIndex}']`);
            if (previewCell) {
                previewCell.classList.add(previewClass);
            }
        }
    }
    if (isValid){
        infoDisplay.textContent = 'Posição Válida!';
    }else{
        infoDisplay.textContent = 'Posição inválida! Navios não podem ficar colados ou ultrapassar limites.';
    }
}

//Limpa o preview visual 
function clearPreview() {
    const previewCells = document.querySelectorAll('#player .ship-preview-valid, #player .invalid-drop');
    previewCells.forEach(cell => {
        cell.classList.remove('ship-preview-valid', 'invalid-drop');
    });
    infoDisplay.textContent = 'Posicione seus navios e clique em START';
}

function dropShip(e) {
    const idx  = Number(e.target.id);
    const ship = ships[draggedShip.id];
    const row  = Math.floor(idx / width);
    const col  = idx % width;
    const isVertical = angle !== 0;

    clearPreview();

    // Verifica se a posição é válida com a nova regra de espaçamento
    if (!canPlaceShip(idx, ship.length, isVertical)) {
        return; // Não posiciona o navio e mantém o draggedShip
    }

    // Marca visualmente no front (se válido)
    for (let i = 0; i < ship.length; i++) {
        const cellIndex = isVertical ? idx + i * width : idx + i;
        document.querySelector(`#player .block[id='${cellIndex}']`)
            .classList.add(ship.name, 'taken');
    }

    // Armazena para enviar depois
    playerShips.push({ row, col, length: ship.length, vertical: angle !== 0 });
    draggedShip.remove();
}

// 5) Definição simples dos barcos (só para previews)
class Ship { constructor(name, length){ this.name = name; this.length = length; } }
const ships = [
    new Ship('destroyer',  2),
    new Ship('submarine',  3),
    new Ship('cruiser',    3),
    new Ship('battleship', 4),
    new Ship('carrier',    5),
];

// 6) Ao clicar em START, envia todas as posições ao back-end
startButton.addEventListener('click', async () => {
    if (playerShips.length !== ships.length) {
        infoDisplay.textContent = 'Coloque todos os navios antes de iniciar!';
        return;
    }
    for (const ps of playerShips) {
        await fetch(`${API}/place`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ps)
        });
    }
    gameStarted = true;
    infoDisplay.textContent = 'Jogo iniciado! Seu turno.';
    turnDisplay.textContent = 'Você';
    const ComputerContainer = document.querySelector('.board-container-computer');
    ComputerContainer.classList.remove('board-container-computer');
    attachAttackHandlers();
});

// 7) Lida com ataque e contra-ataque
function attachAttackHandlers() {
    document.querySelectorAll('#computer .block')
        .forEach(cell => cell.addEventListener('click', handlePlayerAttack));
}

async function handlePlayerAttack(e) {
    if (!gameStarted) return;

    const idx = Number(e.target.id);
    const row = Math.floor(idx / width);
    const col = idx % width;

    // Jogador ataca
    let res  = await fetch(`${API}/attack`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ row, col })
    });
    let data = await res.json();
    e.target.classList.add(data.hit ? 'boom' : 'empty');
    infoDisplay.textContent = data.sunk
        ? console.log(`Você afundou um ${data.sunk}!`)
        : (data.hit ? console.log('Você acertou!') : console.log('Você errou!'));
    if (data.gameOver) return infoDisplay.textContent = 'Você venceu!';

    // Contra-ataque do computador
    turnDisplay.textContent = 'Computador';
    res  = await fetch(`${API}/attack/computer`);
    data = await res.json();
    const compIdx    = data.row * width + data.col;
    const playerCell = document.querySelector(`#player .block[id='${compIdx}']`);
    playerCell.classList.add(data.hit ? 'boom' : 'empty');
    infoDisplay.textContent = data.sunk
        ? `Computador afundou seu ${data.sunk}!`
        : (data.hit ? 'Computador acertou!' : 'Computador errou!');
    if (data.gameOver) return infoDisplay.textContent = 'Computador venceu!';

    turnDisplay.textContent = 'Você';
}