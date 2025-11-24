const inventory = document.getElementById('inventory');

let currentFilter = 'All Vehicles';
let cars = [];

let detailsModalBootstrap = null;  // Bootstrap modal instance

// =========================
// LOAD CARS FROM SERVER
// =========================
function loadCarsFromServer() {
    fetch("http://localhost:8080/cmp257project/CarServlet")
        .then(response => {
            if (!response.ok) throw new Error("Server error: " + response.status);
            return response.json();
        })
        .then(carsFromServer => {
            cars = carsFromServer;
            renderCars(cars);
        })
        .catch(err => console.error("Error loading cars:", err));
}

// =========================
// RENDER CAR CARDS
// =========================
function renderCars(carsToRender) {
    inventory.innerHTML = '';

    let filteredCars =
        currentFilter === "All Vehicles"
            ? carsToRender
            : carsToRender.filter(car => car.category === currentFilter);

    if (filteredCars.length === 0) {
        inventory.innerHTML =
            '<p style="color:#b0b0b0;font-size:1.2rem;margin-top:2rem;text-align:center;">No vehicles found in this category.</p>';
        return;
    }

    filteredCars.forEach((car, index) => {
        const cardDiv = document.createElement('div');
        cardDiv.className = 'car-card';
        cardDiv.dataset.index = cars.indexOf(car);

        cardDiv.innerHTML = `
            <div class="car-image"
                 style="background-image:url('${car.image}');
                        background-size:cover;
                        background-position:center;">
            </div>

            <div class="car-info">
                <div class="car-brand">${car.brand}</div>
                <div class="car-model">${car.model}</div>
                <div class="car-year">${car.year}</div>

                <div class="car-specs">
                    <div class="spec">
                        <span class="spec-label">Engine</span>
                        <span class="spec-value">${car.engine}</span>
                    </div>
                    <div class="spec">
                        <span class="spec-label">Trans</span>
                        <span class="spec-value">${car.transmission}</span>
                    </div>
                </div>

				<div class="car-price">

				    <!-- FIRST ROW: PRICE -->
				    <div class="price-row">
				        <div class="price">AED ${car.price}/Day</div>
				    </div>

				    <!-- SECOND ROW: BUTTONS -->
				    <div class="button-row">
				        <button class="view-btn" data-index="${cars.indexOf(car)}">View Details</button>
				        <button class="book-btn" data-index="${cars.indexOf(car)}">Book Now</button>
				    </div>

				</div>

            </div>
        `;

        inventory.appendChild(cardDiv);
    });
}

// =========================
// FILTER BUTTONS
// =========================
const filterButtons = document.querySelectorAll('.filter-btn');
filterButtons.forEach(button => {
    button.addEventListener('click', () => {
        filterButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');

        currentFilter = button.textContent.trim();
        renderCars(cars);
    });
});

// =========================
// VIEW DETAILS + BOOK NOW
// =========================
document.addEventListener("click", function (e) {

    // View Details
    if (e.target.classList.contains("view-btn")) {
        const index = e.target.dataset.index;
        openDetailsModal(cars[index]);
    }

    // Book Now
    if (e.target.classList.contains("book-btn")) {
        const index = e.target.dataset.index;
        const car = cars[index];

        localStorage.setItem('selectedCarForBooking', JSON.stringify(car));
        window.location.href = 'form.html';
    }
});

// =========================
// OPEN MODAL
// =========================
function openDetailsModal(car) {
    document.getElementById("detailsImage").src = car.image;
    document.getElementById("detailsTitle").textContent = `${car.brand} ${car.model}`;
    document.getElementById("detailsCategory").textContent = car.category;

    document.getElementById("detailsYear").textContent = car.year;
    document.getElementById("detailsEngine").textContent = car.engine;
    document.getElementById("detailsTrans").textContent = car.transmission;
    document.getElementById("detailsDrive").textContent = car.drivetrain;
    document.getElementById("detailsFuel").textContent = car.fuelType;
    document.getElementById("detailsPrice").textContent = car.price;

    const featuresList = document.getElementById("detailsFeatures");
    featuresList.innerHTML = "";
    (car.features || "").split(",").forEach(f => {
        if (f.trim()) {
            let li = document.createElement("li");
            li.textContent = f.trim();
            featuresList.appendChild(li);
        }
    });

    const extra = document.getElementById("detailsExtra");
    extra.innerHTML = "";
    if (car.mileage) extra.innerHTML += `<p><strong>Mileage:</strong> ${car.mileage} km/L</p>`;
    if (car.topSpeed) extra.innerHTML += `<p><strong>Top Speed:</strong> ${car.topSpeed} km/h</p>`;
    if (car.trunkSpaceLiters) extra.innerHTML += `<p><strong>Trunk Space:</strong> ${car.trunkSpaceLiters} L</p>`;
    if (car.seatingCapacity) extra.innerHTML += `<p><strong>Seating Capacity:</strong> ${car.seatingCapacity}</p>`;

    if (!detailsModalBootstrap) {
        detailsModalBootstrap = new bootstrap.Modal(document.getElementById("detailsModal"));
    }
    detailsModalBootstrap.show();
}

// =========================
// INIT
// =========================
loadCarsFromServer();
