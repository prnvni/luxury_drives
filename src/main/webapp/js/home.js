// popular cars carousel - dynamic data from database

let CARS = [];
let currentCarIndex = 0; 

const carContainer = document.getElementById('car-carousel-container');
const detailsCard = document.getElementById('car-details-card');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');


// Fetches car data from CarServlet and initializes the carousel
function loadFeaturedCars() {
    fetch("http://localhost:8080/cmp257project/CarServlet")
	

        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch cars");
            return response.json();
        })
        .then(data => {
            // Filter for Sports / Luxury
            CARS = data.filter(car => car.category === 'Sports' || car.category === 'Luxury');

            if (CARS.length > 0) {
                currentCarIndex = (CARS.length > 1 ? 1 : 0);
                renderCarousel();
            } else {
                carContainer.innerHTML =
                    '<p style="color:white; text-align:center; margin-top:50px;">No cars available to display.</p>';
                detailsCard.innerHTML = '';
            }
        })
        .catch(err => {
            console.error("Error loading cars:", err);
            carContainer.innerHTML = '<p style="color:white; text-align:center;">Error loading popular cars.</p>';
        });
}



// Renders the car images into the carousel container
function renderCarousel() {
    carContainer.innerHTML = ''; 
    
    CARS.forEach((car, index) => {
        const item = document.createElement('div');
        item.className = 'carousel-item';
        item.setAttribute('data-index', index);
        
        const img = document.createElement('img');

        // USE IMAGE PATH EXACTLY FROM SQL
        img.src = car.image;
        img.alt = car.brand + ' ' + car.model;

        // fallback if image file missing
        img.onerror = function () {
            this.src = 'https://placehold.co/900x400/333/fff?text=Image+Not+Found';
        };

        item.appendChild(img);
        carContainer.appendChild(item);
    });
    
    updateCarouselDisplay(currentCarIndex);
}



// Updates which images are displayed as prev/active/next
function updateCarouselDisplay(activeIndex) {
    if (CARS.length === 0) return;

    const items = carContainer.querySelectorAll('.carousel-item');
    const total = CARS.length;
    
    const prevIndex = (activeIndex - 1 + total) % total;
    const nextIndex = (activeIndex + 1) % total;

    items.forEach(item => {
        item.className = 'carousel-item';
        const index = parseInt(item.getAttribute('data-index'));

        if (index === activeIndex) {
            item.classList.add('active');
        } else if (index === prevIndex) {
            item.classList.add('prev');
        } else if (index === nextIndex) {
            item.classList.add('next');
        }
    });

    updateDetails(activeIndex);
}



// Updates the right-side details card
function updateDetails(index) {
    const car = CARS[index];

    let specLabel = 'Category';
    let specValue = car.category;
    let specIcon = 'fa-car';

    if (car.topSpeed) {
        specLabel = 'Max Speed';
        specValue = car.topSpeed + ' km/h';
        specIcon = 'fa-tachometer-alt';
    } else if (car.mileage) {
        specLabel = 'Mileage';
        specValue = car.mileage + ' km/l';
        specIcon = 'fa-gas-pump';
    } else if (car.seatingCapacity) {
        specLabel = 'Capacity';
        specValue = car.seatingCapacity + ' Seats';
        specIcon = 'fa-users';
    } else if (car.trunkSpaceLiters) {
        specLabel = 'Trunk Space';
        specValue = car.trunkSpaceLiters + ' L';
        specIcon = 'fa-suitcase';
    }

    detailsCard.innerHTML = `
        <div class="car-name">${car.brand} ${car.model}</div>
        <div class="car-details-card">
            <div class="detail-item">
                <i class="fas ${specIcon}"></i>
                <span class="detail-value">${specValue}</span>
                <span class="detail-label">${specLabel}</span>
            </div>
            <div class="detail-item">
                <i class="fas fa-bolt"></i>
                <span class="detail-value">${car.engine}</span>
                <span class="detail-label">Engine</span>
            </div>
            <div class="detail-item">
                <i class="fas fa-cogs"></i>
                <span class="detail-value">${car.transmission}</span>
                <span class="detail-label">Transmission</span>
            </div>
        </div>
        <div class="car-price">
            From AED ${car.price} / day
        </div>
    `;
}



// Carousel controls
function nextCar() {
    if (CARS.length === 0) return;
    currentCarIndex = (currentCarIndex + 1) % CARS.length;
    updateCarouselDisplay(currentCarIndex);
}

function prevCar() {
    if (CARS.length === 0) return;
    currentCarIndex = (currentCarIndex - 1 + CARS.length) % CARS.length;
    updateCarouselDisplay(currentCarIndex);
}


// Initialize everything
document.addEventListener('DOMContentLoaded', () => {
    if (carContainer && detailsCard) {
        loadFeaturedCars();
        
        if (prevBtn) prevBtn.addEventListener('click', prevCar);
        if (nextBtn) nextBtn.addEventListener('click', nextCar);
    }
});
