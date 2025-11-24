document.addEventListener("DOMContentLoaded", function () {

    // ============================
    // PAGE NAVIGATION
    // ============================
    const bookingsLink = document.querySelector('[data-page="bookings"]');
    const dashboardLink = document.querySelector('[data-page="dashboard"]');

    if (dashboardLink) {
        dashboardLink.addEventListener("click", (e) => {
            e.preventDefault();

            document.getElementById('bookingsPage').classList.add('hidden');
            document.getElementById('dashboardPage').classList.remove('hidden');

            document.querySelectorAll('.topbar-nav-link').forEach(l => l.classList.remove('active'));
            dashboardLink.classList.add('active');

            loadDashboardStats();
        });
    }

    if (bookingsLink) {
        bookingsLink.addEventListener("click", (e) => {
            e.preventDefault();

            document.getElementById('dashboardPage').classList.add('hidden');
            document.getElementById('bookingsPage').classList.remove('hidden');

            document.querySelectorAll('.topbar-nav-link').forEach(l => l.classList.remove('active'));
            bookingsLink.classList.add('active');

            loadBookings(); // LOAD BOOKINGS ON CLICK
        });
    }

    // ============================
    // LIGHT / DARK MODE
    // ============================
	const lightModeBtn = document.getElementById('lightModeToggle');
	const body = document.body;

	// Load saved preference
	const savedMode = localStorage.getItem('lightMode');
	if (savedMode === 'enabled') {
	    body.classList.add('light-mode');
	    lightModeBtn.innerHTML = '<i class="fas fa-sun"></i> Light Mode';
	}

	if (lightModeBtn) {
	    lightModeBtn.addEventListener('click', () => {

	        body.classList.toggle('light-mode');

	        if (body.classList.contains('light-mode')) {
	            lightModeBtn.innerHTML = '<i class="fas fa-sun"></i> Light Mode';
	            localStorage.setItem('lightMode', 'enabled');
	        } else {
	            lightModeBtn.innerHTML = '<i class="fas fa-moon"></i> Dark Mode';
	            localStorage.setItem('lightMode', 'disabled');
	        }
	    });
	}


    // ============================
    // LOAD DASHBOARD STATS
    // ============================
	function loadDashboardStats() {

	    // Fetch cars
	    fetch("../CarServlet")
	        .then(response => response.json())
	        .then(cars => {
	            document.getElementById('totalCars').textContent = cars.length;
	            document.getElementById('availableCars').textContent = cars.length;
	        })
	        .catch(err => console.error("Error loading cars:", err));

	    // Fetch bookings
	    fetch("../api/bookings")
	        .then(response => response.json())
	        .then(bookings => {
	            document.getElementById('bookedCars').textContent = bookings.length;
	        })
	        .catch(err => console.error("Error loading bookings:", err));
	}


    loadDashboardStats();

    // ============================
    // FETCH ALL BOOKINGS
    // ============================
    function loadBookings() {
        fetch("../api/bookings")
            .then(res => {
                if (!res.ok) throw new Error("Failed to load bookings");
                return res.json();
            })
            .then(data => renderBookings(data))
            .catch(err => console.error("Error fetching bookings:", err));
    }

	// ==============================================
	// FETCH & SHOW LATEST 3 BOOKINGS IN DASHBOARD
	// ==============================================
	function loadRecentBookings() {
	    fetch("../api/bookings")
	        .then(r => r.json())
	        .then(bookings => {

	            // Sort bookings: newest first
	            bookings.sort((a, b) => b.id - a.id);

	            // Take latest 3
	            const latestThree = bookings.slice(0, 3);

	            const tbody = document.getElementById("recentBookingsBody");
	            tbody.innerHTML = "";

	            latestThree.forEach(b => {
	                tbody.innerHTML += `
	                    <tr>
	                        <td>#BK${b.id}</td>
	                        <td>${b.fullName}</td>
	                        <td>${b.carModel}</td>
	                        <td>${b.startDate}</td>
	                        <td><span class="status-badge">${b.status}</span></td>
	                       
	                    </tr>
	                `;
	            });
	        })
	        .catch(err => console.error("Error loading recent bookings:", err));
	}

	// Call it on page load
	loadRecentBookings();

    // ============================
    // RENDER BOOKINGS INTO TABLE
    // ============================
    function renderBookings(bookings) {
        const tbody = document.querySelector("#bookingsPage table.data-table tbody");
        tbody.innerHTML = "";

        if (!bookings || bookings.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" style="text-align:center; padding:20px;">No bookings found</td>
                </tr>`;
            return;
        }

		bookings.forEach(b => {
		        tbody.innerHTML += `
		            <tr>
		                <td>#BK${b.id}</td>
		                <td>${b.fullName}</td>
		                <td>${b.carModel}</td>
		                <td>${b.startDate}</td>
		                <td>${b.endDate}</td>
		                <td>${b.pickupLocation || "—"}</td>    <!-- FIXED -->
		                <td><span class="status-badge">${b.status}</span></td>
		            </tr>
		        `;
		    });
    }

});
