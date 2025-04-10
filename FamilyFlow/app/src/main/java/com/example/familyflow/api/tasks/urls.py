from django.urls import path
from .views import TaskListCreateView, TaskRetrieveUpdateDeleteView, TasksByRoomTypeView, DeleteTasksByRoomTypeView

urlpatterns = [
    # Matches the endpoints in your TaskApiService.kt
    path('tasks/', TaskListCreateView.as_view(), name='task-list'),
    path('tasks/<int:pk>/', TaskRetrieveUpdateDeleteView.as_view(), name='task-detail'),
    # Add the missing endpoints for room type operations
    path('tasks/room/<str:room_type>/', TasksByRoomTypeView.as_view(), name='tasks-by-room'),
    path('tasks/room/<str:room_type>/delete/', DeleteTasksByRoomTypeView.as_view(), name='delete-tasks-by-room'),
]