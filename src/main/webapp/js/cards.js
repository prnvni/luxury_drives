// find the element 'inventory' to add the grid of cars
const inventory = document.getElementById('inventory');
// set initial filter for all vehicles
let currentFilter = 'All Vehicles';
//create a list cars to append the cars from the database
let cars = [];

 // bootstrap modal instance
let detailsModalBootstrap = null; 

//function to load cars from server
function loadCarsFromServer() {
	//fetch the car details in json format from the servlet
    fetch("http://localhost:8080/cmp257project/CarServlet")
        .then(response => {
            if (!response.ok) throw new Error("Server error: " + response.status); //error handling 
            return response.json();
        })
        .then(carsFromServer => {
            cars = carsFromServer; 
            renderCars(cars); //display the cars on screen
        })
        .catch(err => console.error("Error loading cars:", err));
}

//displaying the cars on screen
function renderCars(carsToRender) {
    inventory.innerHTML = '';
// if "All Vehicles" is selected show every car otherwise-> show only the cars whose category matches the selected filter
    let filteredCars =
        currentFilter === "All Vehicles"
            ? carsToRender
            : carsToRender.filter(car => car.category === currentFilter);

	//if no cars match filter display no vehicles found
    if (filteredCars.length === 0) {
        inventory.innerHTML =
            '<p style="color:#b0b0b0;font-size:1.2rem;margin-top:2rem;text-align:center;">No vehicles found in this category.</p>'; //add neccessary css to match style
        return;
    }

	//for each car make a card
    filteredCars.forEach((car, index) => {
        const cardDiv = document.createElement('div');
        cardDiv.className = 'car-card';
        cardDiv.dataset.index = cars.indexOf(car); 	//set the car index, used for displaying view details mdoal

		//use innerhtml to add the information about car along with its image, this data is from the json response stored in 'cars'
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
		//add the card to grid
        inventory.appendChild(cardDiv);
    });
}

//filter buttons
const filterButtons = document.querySelectorAll('.filter-btn');
filterButtons.forEach(button => {
    button.addEventListener('click', () => {
        filterButtons.forEach(btn => btn.classList.remove('active'));	//make the button active (handling css)
        button.classList.add('active');

        currentFilter = button.textContent.trim();
        renderCars(cars);			//display cars of the selected filter
    });
});


//handle event listeners for 'view details' and 'book now' button
document.addEventListener("click", function (e) {

    // view Details
    if (e.target.classList.contains("view-btn")) {
        const index = e.target.dataset.index;  //get the car's index from its data attribute and open the details modal for that car
        openDetailsModal(cars[index]);
    }

    // book Now
    if (e.target.classList.contains("book-btn")) {    //get the car's index, save the selected car to localStorage (small space inside browser) and redirect the user to the booking form page-> to load the car you interested in the form
        const index = e.target.dataset.index;
        const car = cars[index];

        localStorage.setItem('selectedCarForBooking', JSON.stringify(car));
        window.location.href = 'form.html';
    }
});

//open modal
function openDetailsModal(car) {
	//display details of the specific car
    document.getElementById("detailsImage").src = car.image;
    document.getElementById("detailsTitle").textContent = `${car.brand} ${car.model}`;
    document.getElementById("detailsCategory").textContent = car.category;

    document.getElementById("detailsYear").textContent = car.year;
    document.getElementById("detailsEngine").textContent = car.engine;
    document.getElementById("detailsTrans").textContent = car.transmission;
    document.getElementById("detailsDrive").textContent = car.drivetrain;
    document.getElementById("detailsFuel").textContent = car.fuelType;
    document.getElementById("detailsPrice").textContent = car.price;

	//make a list for the features
    const featuresList = document.getElementById("detailsFeatures");
    featuresList.innerHTML = "";
    (car.features || "").split(",").forEach(f => {
        if (f.trim()) {
            let li = document.createElement("li");
            li.textContent = f.trim();
            featuresList.appendChild(li);
        }
    });

	//get the extra feature depending on category of car
    const extra = document.getElementById("detailsExtra");
    extra.innerHTML = "";
    if (car.mileage) extra.innerHTML += `<p><strong>Mileage:</strong> ${car.mileage} km/L</p>`;
    if (car.topSpeed) extra.innerHTML += `<p><strong>Top Speed:</strong> ${car.topSpeed} km/h</p>`;
    if (car.trunkSpaceLiters) extra.innerHTML += `<p><strong>Trunk Space:</strong> ${car.trunkSpaceLiters} L</p>`;
    if (car.seatingCapacity) extra.innerHTML += `<p><strong>Seating Capacity:</strong> ${car.seatingCapacity}</p>`;
	//set bootstrap
    if (!detailsModalBootstrap) {
        detailsModalBootstrap = new bootstrap.Modal(document.getElementById("detailsModal"));
    }
    detailsModalBootstrap.show();
}

//function call to load the cars and get json response from servlet
loadCarsFromServer();

