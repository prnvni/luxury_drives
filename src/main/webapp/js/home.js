// ========================
// POPULAR CARS
// ========================
const CARS = [
   
    { name:"Ferrari F8 Tributo", img:"../imgs/Ferrari-F8-Tributo.png", maxSpeed:"340 km/h", power:"710 HP", transmission:"Automatic", price:"3500" },
    { name:"Lamborghini Huracán Evo", img:"../imgs/Lamborghini-Huracan-Evo.png", maxSpeed:"325 km/h", power:"640 HP", transmission:"Automatic", price:"4100" },
    { name:"Porsche 911 Turbo S", img:"../imgs/Porsche-911-Turbo-S.png", maxSpeed:"320 km/h", power:"502 HP", transmission:"Manual/Auto", price:"2800" }
];

let currentCarIndex = 1;

// MUST BE GLOBAL
let carContainer, detailsCard, prevBtn, nextBtn;


// ========================
// INIT AFTER DOM LOAD
// ========================
document.addEventListener("DOMContentLoaded", () => {
    carContainer = document.getElementById("car-carousel-container");
    detailsCard = document.getElementById("car-details-card");
    prevBtn = document.getElementById("prev-btn");
    nextBtn = document.getElementById("next-btn");

    if (carContainer && detailsCard && prevBtn && nextBtn) {
        renderCarousel();
        prevBtn.addEventListener("click", prevCar);
        nextBtn.addEventListener("click", nextCar);
    }
});


// ========================
// RENDER CAROUSEL
// ========================
function renderCarousel() {
    carContainer.innerHTML = "";

    CARS.forEach((car, index) => {
        const item = document.createElement("div");
        item.className = "carousel-item";
        item.setAttribute("data-index", index);

        const img = document.createElement("img");
        img.src = car.img;

        item.appendChild(img);
        carContainer.appendChild(item);
    });

    updateCarouselDisplay(currentCarIndex);
}


// ========================
// UPDATE CAR POSITIONS
// ========================
function updateCarouselDisplay(activeIndex) {
    const items = carContainer.querySelectorAll(".carousel-item");
    const total = CARS.length;

    const prevIndex = (activeIndex - 1 + total) % total;
    const nextIndex = (activeIndex + 1) % total;

    items.forEach(item => {
        item.className = "carousel-item";
        const index = parseInt(item.dataset.index);

        if (index === activeIndex) item.classList.add("active");
        else if (index === prevIndex) item.classList.add("prev");
        else if (index === nextIndex) item.classList.add("next");
    });

    updateDetails(activeIndex);
}


// ========================
// UPDATE DETAILS CARD
// ========================
function updateDetails(index) {
    const car = CARS[index];

    detailsCard.innerHTML = `
        <div class="car-name">${car.name}</div>
        <div class="car-details-card">
            <div class="detail-item"><i class="fas fa-tachometer-alt"></i><span>${car.maxSpeed}</span><span>Max Speed</span></div>
            <div class="detail-item"><i class="fas fa-bolt"></i><span>${car.power}</span><span>Power</span></div>
            <div class="detail-item"><i class="fas fa-cogs"></i><span>${car.transmission}</span><span>Transmission</span></div>
        </div>
        <div class="car-price">From AED ${car.price} / day</div>
    `;
}


// ========================
// BUTTONS
// ========================
function nextCar() {
    currentCarIndex = (currentCarIndex + 1) % CARS.length;
    updateCarouselDisplay(currentCarIndex);
}

function prevCar() {
    currentCarIndex = (currentCarIndex - 1 + CARS.length) % CARS.length;
    updateCarouselDisplay(currentCarIndex);
}
