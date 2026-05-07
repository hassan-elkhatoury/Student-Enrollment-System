const state = {
    currentCnie: "CD2387",
    currentStudent: null,
    courses: [],
    capacities: [],
    enrollments: [],
    selectedCourseId: null
};

const elements = {
    studentForm: document.querySelector("#student-form"),
    enrollmentForm: document.querySelector("#enrollment-form"),
    studentCnie: document.querySelector("#student-cnie"),
    courseSelect: document.querySelector("#course-select"),
    coursesList: document.querySelector("#courses-list"),
    enrollmentsTable: document.querySelector("#enrollments-table"),
    enrollmentCount: document.querySelector("#enrollment-count"),
    systemMessage: document.querySelector("#system-message"),
    enrollMessage: document.querySelector("#enroll-message"),
    refreshCourses: document.querySelector("#refresh-courses"),
    studentAvatar: document.querySelector("#student-avatar"),
    studentName: document.querySelector("#student-name"),
    studentEmail: document.querySelector("#student-email"),
    metricCourses: document.querySelector("#metric-courses"),
    metricCredits: document.querySelector("#metric-credits"),
    metricOpen: document.querySelector("#metric-open"),
    metricLocked: document.querySelector("#metric-locked")
};

document.addEventListener("DOMContentLoaded", async () => {
    bindEvents();
    await loadCourses();
    await loadStudentWorkspace(state.currentCnie);
});

function bindEvents() {
    elements.studentForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const cnie = elements.studentCnie.value.trim();
        if (!cnie) {
            showSystemMessage("CNIE is required.", "error");
            return;
        }
        state.currentCnie = cnie;
        await loadStudentWorkspace(cnie);
    });

    elements.enrollmentForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await createEnrollment();
    });

    elements.refreshCourses.addEventListener("click", loadCourses);

    elements.courseSelect.addEventListener("change", () => {
        state.selectedCourseId = Number(elements.courseSelect.value);
        renderCourses();
    });
}

async function loadStudentWorkspace(cnie) {
    showSystemMessage("Loading " + cnie + " dossier.", "");
    try {
        const [student, enrollments] = await Promise.all([
            fetchJson(`/api/students/by-cnie/${encodeURIComponent(cnie)}`),
            fetchJson(`/api/enrollments/student/${encodeURIComponent(cnie)}`)
        ]);

        state.currentStudent = student;
        state.enrollments = enrollments;
        renderStudent();
        renderEnrollments();
        showSystemMessage("Dossier loaded for " + cnie + ".", "success");
    } catch (error) {
        state.currentStudent = null;
        state.enrollments = [];
        renderStudent();
        renderEnrollments();
        showSystemMessage(error.message, "error");
    }
}

async function loadCourses() {
    try {
        const [courses, capacities] = await Promise.all([
            fetchJson("/api/courses"),
            fetchJson("/api/enrollments/courses/capacity")
        ]);

        state.courses = courses;
        state.capacities = capacities;
        if (!state.selectedCourseId && courses.length) {
            state.selectedCourseId = courses[0].id;
        }
        renderCourseOptions();
        renderCourses();
        renderStudent();
    } catch (error) {
        elements.coursesList.innerHTML = `<div class="empty-state">${escapeHtml(error.message)}</div>`;
    }
}

async function createEnrollment() {
    const courseId = Number(elements.courseSelect.value);
    const studentCnie = elements.studentCnie.value.trim();

    if (!studentCnie || !courseId) {
        showInlineMessage("Select a student and course.", "error");
        return;
    }

    showInlineMessage("Submitting enrollment.", "");
    try {
        await fetchJson("/api/enrollments", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ studentCnie, courseId })
        });

        showInlineMessage("Enrollment saved.", "success");
        await loadCourses();
        await loadStudentWorkspace(studentCnie);
    } catch (error) {
        showInlineMessage(error.message, "error");
    }
}

async function cancelEnrollment(enrollmentId) {
    const studentCnie = elements.studentCnie.value.trim();

    try {
        await fetchJson(`/api/enrollments/${enrollmentId}?studentCnie=${encodeURIComponent(studentCnie)}`, {
            method: "DELETE"
        });
        await loadCourses();
        await loadStudentWorkspace(studentCnie);
    } catch (error) {
        showSystemMessage(error.message, "error");
    }
}

function renderStudent() {
    const student = state.currentStudent;
    const name = student ? `${student.firstName} ${student.lastName}` : state.currentCnie;
    const initials = student ? `${student.firstName[0] || ""}${student.lastName[0] || ""}` : state.currentCnie.slice(0, 2);
    const totalCredits = state.enrollments.reduce((sum, enrollment) => {
        const course = state.courses.find(item => item.title === enrollment.courseName);
        return sum + (course ? course.credits : 0);
    }, 0);
    const openCount = state.enrollments.filter(enrollment => enrollment.deletable).length;
    const lockedCount = state.enrollments.length - openCount;

    elements.studentAvatar.textContent = initials.toUpperCase();
    elements.studentName.textContent = name;
    elements.studentEmail.textContent = student ? `${student.cnie} - ${student.email}` : "Student not loaded";
    elements.metricCourses.textContent = state.enrollments.length;
    elements.metricCredits.textContent = totalCredits;
    elements.metricOpen.textContent = openCount;
    elements.metricLocked.textContent = lockedCount;
}

function renderCourses() {
    if (!state.capacities.length) {
        elements.coursesList.innerHTML = `<div class="empty-state">No courses found.</div>`;
        return;
    }

    elements.coursesList.innerHTML = state.capacities.map((course, index) => {
        const percent = Math.min(100, Math.round((course.enrolledCount / course.capacity) * 100));
        const fillClass = percent >= 100 ? "full" : percent >= 67 ? "warning" : "";
        const availableClass = course.availablePlaces === 0 ? "full" : "";
        const selectedClass = course.courseId === state.selectedCourseId ? "selected" : "";

        return `
            <article class="course-card ${selectedClass}" data-course-id="${course.courseId}">
                <div class="course-code">C${String(index + 1).padStart(2, "0")}</div>
                <div class="course-main">
                    <div class="course-title-row">
                        <h4>${escapeHtml(course.courseName)}</h4>
                        <span class="credit-chip">${course.credits} credits</span>
                    </div>
                    <p>${escapeHtml(course.description)}</p>
                    <div class="capacity-track">
                        <div class="capacity-fill ${fillClass}" style="width: ${percent}%"></div>
                    </div>
                    <div class="capacity-meta">
                        <span>${course.enrolledCount}/${course.capacity} seats</span>
                        <span class="seat-chip ${availableClass}">${course.availablePlaces} left</span>
                    </div>
                </div>
            </article>
        `;
    }).join("");

    document.querySelectorAll("[data-course-id]").forEach(card => {
        card.addEventListener("click", () => {
            state.selectedCourseId = Number(card.dataset.courseId);
            elements.courseSelect.value = state.selectedCourseId;
            renderCourses();
        });
    });
}

function renderCourseOptions() {
    elements.courseSelect.innerHTML = state.courses.map(course => `
        <option value="${course.id}">${escapeHtml(course.title)}</option>
    `).join("");

    if (state.selectedCourseId) {
        elements.courseSelect.value = state.selectedCourseId;
    }
}

function renderEnrollments() {
    elements.enrollmentCount.textContent = `${state.enrollments.length} record${state.enrollments.length === 1 ? "" : "s"}`;

    if (!state.enrollments.length) {
        elements.enrollmentsTable.innerHTML = `<div class="empty-state">No enrollment records.</div>`;
        return;
    }

    elements.enrollmentsTable.innerHTML = state.enrollments.map(enrollment => {
        const statusClass = enrollment.deletable ? "open" : "locked";
        const statusLabel = enrollment.deletable ? "Cancelable" : "Locked";
        const pinClass = enrollment.deletable ? "" : "locked";
        const action = enrollment.deletable
            ? `<button class="danger-button" type="button" data-enrollment-id="${enrollment.enrollmentId}">Cancel</button>`
            : `<button type="button" disabled>Closed</button>`;

        return `
            <article class="ledger-row">
                <div class="ledger-pin ${pinClass}"></div>
                <div class="ledger-main">
                    <h4>${escapeHtml(enrollment.courseName)}</h4>
                    <p>${escapeHtml(enrollment.studentCnie)} - ${formatDate(enrollment.date)}</p>
                </div>
                <div class="ledger-actions">
                    <span class="status ${statusClass}">${statusLabel}</span>
                    ${action}
                </div>
            </article>
        `;
    }).join("");

    document.querySelectorAll("[data-enrollment-id]").forEach(button => {
        button.addEventListener("click", () => cancelEnrollment(button.dataset.enrollmentId));
    });
}

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);
    if (!response.ok) {
        throw await readApiError(response);
    }
    if (response.status === 204) {
        return null;
    }
    return response.json();
}

async function readApiError(response) {
    try {
        const body = await response.json();
        return new Error(body.message || `Request failed with status ${response.status}`);
    } catch {
        return new Error(`Request failed with status ${response.status}`);
    }
}

function showSystemMessage(message, type) {
    elements.systemMessage.textContent = message;
    elements.systemMessage.className = `system-message ${type}`;
}

function showInlineMessage(message, type) {
    elements.enrollMessage.textContent = message;
    elements.enrollMessage.className = `inline-message ${type}`;
}

function formatDate(value) {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return date.toLocaleString();
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
