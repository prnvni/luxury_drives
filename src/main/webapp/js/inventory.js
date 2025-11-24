let cars = [];
// Load cars from server
function loadCars() {
    fetch("../CarServlet")
        .then(response => {
            console.log("Response status:", response.status);
            return response.text(); // Get as text first to see what we're receiving
        })

        .then(text => {
            console.log("Raw response:", text); // THIS WILL SHOW YOU THE PROBLEM
            try {
                const data = JSON.parse(text);
                console.log("Parsed data:", data);
                cars = data;
                renderCars(cars);
            } catch (e) {
                console.error("JSON Parse Error:", e);
                console.error("Failed text:", text);
                alert("Error: Server returned invalid JSON. Check console for details.");
            }
        })
        .catch(err => console.error("Error loading cars:", err));
}
// Render cars in table
function renderCars(carsToRender) {
    const tableBody = document.getElementById('carTableBody');
    tableBody.innerHTML = '';
    if (carsToRender.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No cars found</td></tr>';
        return;
    }

    carsToRender.forEach(car => {

        const row = `
            <tr>
                <td>
                    <div class="car-info">
                        <div class="car-image" style="background-image:url('${car.image || ''}'); background-size:cover;"></div>
                        <div class="car-details">
                            <h4>${car.brand} ${car.model}</h4>
                            <p>${car.year} Model</p>
                        </div>
                    </div>
                </td>
                <td>${car.category}</td>
                <td>${car.engine}</td>
                <td>${car.transmission}</td>
                <td>AED ${car.price}</td>
                <td>
                    <div class="action-buttons">
                        <button class="icon-btn-table edit-btn" data-id="${car.id}" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="icon-btn-table delete-btn" data-id="${car.id}" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
        tableBody.innerHTML += row;

    });
}
// Light/Dark Mode
const lightModeToggle = document.getElementById('lightModeToggle');
if (lightModeToggle) {
    lightModeToggle.addEventListener('click', () => {
        document.body.classList.toggle('light-mode');
        if (document.body.classList.contains('light-mode')) {
            lightModeToggle.innerHTML = '<i class="fas fa-sun"></i> Light Mode';
        } else {
            lightModeToggle.innerHTML = '<i class="fas fa-moon"></i> Dark Mode';
        }
    });
}



// Modal elements

const addCarModal = document.getElementById('addCarModal');

const editCarModal = document.getElementById('editCarModal');

const addCarForm = document.getElementById('addCarForm');

const editCarForm = document.getElementById('editCarForm');

// Open Add Modal
document.getElementById('addCarBtn').addEventListener('click', () => addCarModal.classList.add('active'));

// Close Modals

document.getElementById('closeAddModal').addEventListener('click', () => addCarModal.classList.remove('active'));

document.getElementById('cancelAdd').addEventListener('click', () => addCarModal.classList.remove('active'));

document.getElementById('closeEditModal').addEventListener('click', () => editCarModal.classList.remove('active'));

document.getElementById('cancelEdit').addEventListener('click', () => editCarModal.classList.remove('active'));

// ADD CAR
addCarForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const formData = new FormData(addCarForm);
    fetch("../AddCarServlet", { method: "POST", body: formData })
        .then(res => res.text())
        .then(msg => {
            alert("Car added successfully!");
            addCarModal.classList.remove('active');
            addCarForm.reset();
            loadCars();
        })
        .catch(err => alert("Error adding car"));

});
// EDIT & DELETE - Event delegation
document.getElementById('carTableBody').addEventListener('click', (e) => {
    const editBtn = e.target.closest('.edit-btn');
    const deleteBtn = e.target.closest('.delete-btn');
    // EDIT
    if (editBtn) {
        const carId = editBtn.dataset.id;
        const car = cars.find(c => c.id == carId);
        if (car) {
            document.getElementById('editCarId').value = car.id;
            document.getElementById('editBrand').value = car.brand;
            document.getElementById('editModel').value = car.model;
            document.getElementById('editYear').value = car.year;
            document.getElementById('editCategory').value = car.category;
			updateEditExtraField(car.category);
			if (car.category === "Luxury") {
	            editExtraFieldInput.value = car.mileage;
	        }
	        else if (car.category === "Sports") {
	            editExtraFieldInput.value = car.topSpeed;
	        }
	        else if (car.category === "Sedan") {
	            editExtraFieldInput.value = car.trunkSpace;
	        }
	        else if (car.category === "SUV") {
	            editExtraFieldInput.value = car.seatingCapacity;
	        }
            document.getElementById('editPrice').value = car.price;
            document.getElementById('editEngine').value = car.engine;
            document.getElementById('editTransmission').value = car.transmission;
            document.getElementById('editDrivetrain').value = car.drivetrain;
            document.getElementById('editFuelType').value = car.fuelType;
            document.getElementById('editColor').value = car.color;
            document.getElementById('editInterior').value = car.interior;
            document.getElementById('editFeatures').value = car.features;
            editCarModal.classList.add('active');
        }
    }
    // DELETE
    if (deleteBtn) {
        const carId = deleteBtn.dataset.id;
        if (confirm("Are you sure you want to delete this car?")) {
            fetch("../DeleteCarServlet?id=" + carId, { method: "DELETE" })
                .then(() => {
                    alert("Car deleted!");
                    loadCars();
                })
                .catch(err => alert("Error deleting car"));
        }
    }
});
// EDIT CAR SUBMIT
editCarForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const formData = new FormData(editCarForm);
    fetch("../EditCarServlet", { method: "POST", body: formData })
        .then(res => res.text())
        .then(msg => {
            alert("Car updated successfully!");
            editCarModal.classList.remove('active');
            loadCars();
        })
        .catch(err => alert("Error updating car"));
});


const categorySelect = document.querySelector('select[name="category"]');
const extraFieldContainer = document.getElementById("extraFieldContainer");
const extraFieldLabel = document.getElementById("extraFieldLabel");
const extraFieldInput = document.getElementById("extraFieldInput");
categorySelect.addEventListener("change", () => {
    const cat = categorySelect.value;
    extraFieldContainer.style.display = "none";
    extraFieldInput.name = "";  // Prevent sending useless data
    if (cat === "Luxury") {
        extraFieldContainer.style.display = "block";
        extraFieldLabel.textContent = "Mileage";
        extraFieldInput.placeholder = "Mileage (km)";
        extraFieldInput.name = "mileage";
    }
    else if (cat === "Sports") {
        extraFieldContainer.style.display = "block";
        extraFieldLabel.textContent = "Top Speed (km/h)";
        extraFieldInput.placeholder = "Top Speed";
        extraFieldInput.name = "top_speed";
    }

    else if (cat === "Sedan") {
        extraFieldContainer.style.display = "block";
        extraFieldLabel.textContent = "Trunk Space (Liters)";
        extraFieldInput.placeholder = "Trunk Space in Liters";
        extraFieldInput.name = "trunk_space_liters";
    }
    else if (cat === "SUV") {
        extraFieldContainer.style.display = "block";
        extraFieldLabel.textContent = "Seating Capacity";
        extraFieldInput.placeholder = "Number of seats";
        extraFieldInput.name = "seating_capacity";
    }
});


const editCategorySelect = document.getElementById("editCategory");
const editExtraFieldContainer = document.getElementById("editExtraFieldContainer");
const editExtraFieldLabel = document.getElementById("editExtraFieldLabel");
const editExtraFieldInput = document.getElementById("editExtraFieldInput");

function updateEditExtraField(category) {
    editExtraFieldContainer.style.display = "none";
    editExtraFieldInput.name = "";
    if (category === "Luxury") {
        editExtraFieldContainer.style.display = "block";
        editExtraFieldLabel.textContent = "Mileage (km)";
        editExtraFieldInput.placeholder = "Mileage";
        editExtraFieldInput.name = "mileage";
    }

    else if (category === "Sports") {
        editExtraFieldContainer.style.display = "block";
        editExtraFieldLabel.textContent = "Top Speed (km/h)";
        editExtraFieldInput.placeholder = "Top Speed";
        editExtraFieldInput.name = "top_speed";
    }

    else if (category === "Sedan") {
        editExtraFieldContainer.style.display = "block";
        editExtraFieldLabel.textContent = "Trunk Space (Liters)";
        editExtraFieldInput.placeholder = "Trunk Space L";
        editExtraFieldInput.name = "trunk_space_liters";
    }

    else if (category === "SUV") {
        editExtraFieldContainer.style.display = "block";
        editExtraFieldLabel.textContent = "Seating Capacity";
        editExtraFieldInput.placeholder = "Seats";
        editExtraFieldInput.name = "seating_capacity";
    }
}

// When category is changed manually

editCategorySelect.addEventListener("change", () => {
    updateEditExtraField(editCategorySelect.value);

});

loadCars();
