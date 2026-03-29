const taskForm = document.getElementById('task-form');
const titleInput = document.getElementById('title');
const descriptionInput = document.getElementById('description');
const taskList = document.getElementById('task-list');
const taskCount = document.getElementById('task-count');
const formMessage = document.getElementById('form-message');

function showMessage(text, type = 'success') {
  formMessage.innerHTML = `<div class="alert alert-${type} py-2">${text}</div>`;
  setTimeout(() => {
    formMessage.innerHTML = '';
  }, 3500);
}

function createTaskItem(task) {
  const item = document.createElement('div');
  item.className = 'list-group-item d-flex flex-column flex-sm-row justify-content-between align-items-start gap-3';
  item.innerHTML = `
    <div class="ms-2 me-auto">
      <div class="fw-bold ${task.completed ? 'task-completed' : ''}">${task.title}</div>
      <p class="mb-1 ${task.completed ? 'task-completed' : ''}">${task.description || 'No description provided.'}</p>
      <span class="badge ${task.completed ? 'bg-success' : 'bg-secondary'} task-status">${task.completed ? 'Completed' : 'Pending'}</span>
    </div>
    <div class="d-flex gap-2 align-items-center">
      <button class="btn btn-sm ${task.completed ? 'btn-outline-warning' : 'btn-outline-success'}" data-id="${task.id}">${task.completed ? 'Mark Pending' : 'Mark Complete'}</button>
    </div>
  `;

  const actionButton = item.querySelector('button');
  actionButton.addEventListener('click', async () => {
    try {
      const response = await fetch(`/api/tasks/${task.id}?completed=${!task.completed}`, {
        method: 'PATCH'
      });
      if (!response.ok) {
        throw new Error('Unable to update task status.');
      }
      loadTasks();
    } catch (error) {
      showMessage(error.message, 'danger');
    }
  });

  return item;
}

async function loadTasks() {
  try {
    const response = await fetch('/api/tasks');
    const tasks = await response.json();
    taskList.innerHTML = '';
    tasks.forEach(task => taskList.appendChild(createTaskItem(task)));
    taskCount.textContent = `${tasks.length} tasks`;
  } catch (error) {
    taskList.innerHTML = `<div class="alert alert-danger">Unable to load tasks.</div>`;
  }
}

taskForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const title = titleInput.value.trim();
  const description = descriptionInput.value.trim();

  if (!title) {
    showMessage('Title is required.', 'warning');
    return;
  }

  try {
    const response = await fetch('/api/tasks', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description })
    });
    if (!response.ok) {
      throw new Error('Failed to add task.');
    }
    titleInput.value = '';
    descriptionInput.value = '';
    showMessage('Task added successfully.');
    await loadTasks();
  } catch (error) {
    showMessage(error.message, 'danger');
  }
});

loadTasks();
