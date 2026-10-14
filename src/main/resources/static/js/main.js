/**
 * VELVET BEAUTY - Client-side interactive helpers
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Quick test login credentials filler
    const quickLoginBtns = document.querySelectorAll('.quick-login-btn');
    quickLoginBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const user = this.dataset.user;
            const pass = this.dataset.pass;
            const userInput = document.getElementById('loginUsername');
            const passInput = document.getElementById('loginPassword');
            if (userInput && passInput) {
                userInput.value = user;
                passInput.value = pass;
            }
        });
    });

    // 2. Interactive Booking Price Calculator
    const serviceSelect = document.getElementById('bookingServiceSelect');
    const customSelect = document.getElementById('bookingCustomizationSelect');
    const discountRateInput = document.getElementById('userDiscountRate');
    const priceDisplay = document.getElementById('summaryFinalPrice');
    const discountDisplay = document.getElementById('summaryDiscount');
    const baseDisplay = document.getElementById('summaryBasePrice');

    function updateBookingSummary() {
        if (!serviceSelect || !priceDisplay) return;

        const selectedOption = serviceSelect.options[serviceSelect.selectedIndex];
        if (!selectedOption || !selectedOption.dataset.basePrice) return;

        const basePrice = parseFloat(selectedOption.dataset.basePrice) || 0;
        let surcharge = 0;

        if (customSelect && customSelect.selectedIndex >= 0) {
            const customOption = customSelect.options[customSelect.selectedIndex];
            if (customOption && customOption.dataset.surcharge) {
                surcharge = parseFloat(customOption.dataset.surcharge) || 0;
            }
        }

        const subtotal = basePrice + surcharge;
        const discountRate = parseFloat(discountRateInput ? discountRateInput.value : 0) || 0;
        const discountAmount = Math.round(subtotal * discountRate * 100) / 100;
        const finalPrice = Math.max(0, Math.round((subtotal - discountAmount) * 100) / 100);

        if (baseDisplay) baseDisplay.textContent = 'Rs. ' + subtotal.toFixed(2);
        if (discountDisplay) discountDisplay.textContent = '-Rs. ' + discountAmount.toFixed(2);
        if (priceDisplay) priceDisplay.textContent = 'Rs. ' + finalPrice.toFixed(2);
    }

    if (serviceSelect) {
        serviceSelect.addEventListener('change', function () {
            // Update customization options for selected service category
            const category = this.options[this.selectedIndex].dataset.category;
            populateCustomizations(category);
            updateBookingSummary();
        });
    }

    if (customSelect) {
        customSelect.addEventListener('change', updateBookingSummary);
    }

    function populateCustomizations(category) {
        if (!customSelect) return;
        customSelect.innerHTML = '';

        let options = [];
        if (category === 'HAIR') {
            options = [
                { label: 'Short (Standard)', surcharge: 0 },
                { label: 'Medium (+ Rs. 3000.00)', surcharge: 3000 },
                { label: 'Long (+ Rs. 6000.00)', surcharge: 6000 },
                { label: 'Extra Long (+ Rs. 10500.00)', surcharge: 10500 }
            ];
        } else if (category === 'SKIN') {
            options = [
                { label: 'Standard Hydrating (Included)', surcharge: 0 },
                { label: 'Organic Herbal Elixir (+ Rs. 4500.00)', surcharge: 4500 },
                { label: 'Hyaluronic Glass Skin (+ Rs. 7500.00)', surcharge: 7500 },
                { label: '24K Gold Luxury Radiance (+ Rs. 13500.00)', surcharge: 13500 }
            ];
        } else if (category === 'NAIL') {
            options = [
                { label: 'Classic High-Gloss (Included)', surcharge: 0 },
                { label: 'Long-Wear Gel Polish (+ Rs. 3600.00)', surcharge: 3600 },
                { label: 'Acrylic Sculpted Extensions (+ Rs. 7500.00)', surcharge: 7500 },
                { label: 'Hand-Painted Designer Nail Art (+ Rs. 10500.00)', surcharge: 10500 }
            ];
        } else {
            options = [{ label: 'Standard Service Protocol', surcharge: 0 }];
        }

        options.forEach(opt => {
            const el = document.createElement('option');
            el.value = opt.label;
            el.textContent = opt.label;
            el.dataset.surcharge = opt.surcharge;
            customSelect.appendChild(el);
        });
    }

    // Trigger initial calculation if on booking page
    if (serviceSelect && serviceSelect.selectedIndex >= 0) {
        const cat = serviceSelect.options[serviceSelect.selectedIndex].dataset.category;
        populateCustomizations(cat);
        updateBookingSummary();
    }

    // 3. Dynamic Stylist Slot Loader
    const stylistSelect = document.getElementById('bookingStylistSelect');
    const dateInput = document.getElementById('bookingDateInput');
    const timeSlotSelect = document.getElementById('bookingTimeSlotSelect');

    function fetchAvailableSlots() {
        if (!stylistSelect || !dateInput || !timeSlotSelect) return;
        const stylistId = stylistSelect.value;
        const date = dateInput.value;
        if (!stylistId || !date) return;

        fetch(`/appointments/slots?stylistId=${encodeURIComponent(stylistId)}&date=${encodeURIComponent(date)}`)
            .then(res => res.json())
            .then(slots => {
                timeSlotSelect.innerHTML = '';
                if (slots.length === 0) {
                    const opt = document.createElement('option');
                    opt.textContent = 'No slots available on this date';
                    opt.disabled = true;
                    timeSlotSelect.appendChild(opt);
                } else {
                    slots.forEach(slot => {
                        const opt = document.createElement('option');
                        opt.value = slot;
                        opt.textContent = slot;
                        timeSlotSelect.appendChild(opt);
                    });
                }
            })
            .catch(() => {});
    }

    if (stylistSelect) stylistSelect.addEventListener('change', fetchAvailableSlots);
    if (dateInput) dateInput.addEventListener('change', fetchAvailableSlots);

    // 4. Booking Form Validation
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        bookingForm.addEventListener('submit', function (e) {
            let valid = true;
            const srv = document.getElementById('bookingServiceSelect');
            const sty = document.getElementById('bookingStylistSelect');
            const dte = document.getElementById('bookingDateInput');
            const slt = document.getElementById('bookingTimeSlotSelect');
            const nts = document.getElementById('bookingNotes');

            const srvErr = document.getElementById('serviceErr');
            const styErr = document.getElementById('stylistErr');
            const dteErr = document.getElementById('dateErr');
            const sltErr = document.getElementById('slotErr');
            const ntsErr = document.getElementById('notesErr');

            if (srv && (!srv.value || srv.value === '')) {
                srv.classList.add('is-invalid');
                if (srvErr) srvErr.style.display = 'block';
                valid = false;
            } else if (srv) {
                srv.classList.remove('is-invalid');
                if (srvErr) srvErr.style.display = 'none';
            }

            if (sty && (!sty.value || sty.value === '')) {
                sty.classList.add('is-invalid');
                if (styErr) styErr.style.display = 'block';
                valid = false;
            } else if (sty) {
                sty.classList.remove('is-invalid');
                if (styErr) styErr.style.display = 'none';
            }

            if (dte) {
                const today = new Date().toISOString().split('T')[0];
                if (!dte.value || dte.value < today) {
                    dte.classList.add('is-invalid');
                    if (dteErr) dteErr.style.display = 'block';
                    valid = false;
                } else {
                    dte.classList.remove('is-invalid');
                    if (dteErr) dteErr.style.display = 'none';
                }
            }

            if (slt && (!slt.value || slt.value.startsWith('No slots'))) {
                slt.classList.add('is-invalid');
                if (sltErr) sltErr.style.display = 'block';
                valid = false;
            } else if (slt) {
                slt.classList.remove('is-invalid');
                if (sltErr) sltErr.style.display = 'none';
            }

            if (nts && nts.value) {
                if (nts.value.length > 500 || nts.value.includes(';;')) {
                    nts.classList.add('is-invalid');
                    if (ntsErr) ntsErr.style.display = 'block';
                    valid = false;
                } else {
                    nts.classList.remove('is-invalid');
                    if (ntsErr) ntsErr.style.display = 'none';
                }
            }

            if (!valid) {
                e.preventDefault();
            }
        });
    }

    // 5. Reschedule Form Validation
    const rescheduleForm = document.getElementById('rescheduleForm');
    if (rescheduleForm) {
        rescheduleForm.addEventListener('submit', function (e) {
            let valid = true;
            const dte = rescheduleForm.querySelector('#bookingDateInput');
            const slt = rescheduleForm.querySelector('#bookingTimeSlotSelect');
            const dteErr = document.getElementById('reschedDateErr');
            const sltErr = document.getElementById('reschedSlotErr');

            if (dte) {
                const today = new Date().toISOString().split('T')[0];
                if (!dte.value || dte.value < today) {
                    dte.classList.add('is-invalid');
                    if (dteErr) dteErr.style.display = 'block';
                    valid = false;
                } else {
                    dte.classList.remove('is-invalid');
                    if (dteErr) dteErr.style.display = 'none';
                }
            }

            if (slt && (!slt.value || slt.value.startsWith('No slots'))) {
                slt.classList.add('is-invalid');
                if (sltErr) sltErr.style.display = 'block';
                valid = false;
            } else if (slt) {
                slt.classList.remove('is-invalid');
                if (sltErr) sltErr.style.display = 'none';
            }

            if (!valid) {
                e.preventDefault();
            }
        });
    }
});
