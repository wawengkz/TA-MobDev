from rest_framework import serializers
from .models import Task

class TaskSerializer(serializers.ModelSerializer):
    # Add serializer fields to convert between camelCase (API) and snake_case (Django model)
    assignedTo = serializers.CharField(source='assigned_to', allow_null=True, required=False)
    isDone = serializers.BooleanField(source='is_done', default=False)
    roomType = serializers.CharField(source='room_type')
    days = serializers.SerializerMethodField()

    class Meta:
        model = Task
        fields = ['id', 'name', 'days', 'assignedTo', 'isDone', 'roomType']

    def get_days(self, obj):
        """Convert the comma-separated days string to a list for the API response"""
        if not obj.days or obj.days.strip() == '':
            return []
        return obj.days.split(',')

    def to_internal_value(self, data):
        """Handle converting the days set to a comma-separated string for storage"""
        days_data = data.get('days')

        ret = super().to_internal_value(data)

        # Ensure we have a proper days value
        if days_data:
            if isinstance(days_data, (list, set)):
                # Convert list or set to comma-separated string
                ret['days'] = ','.join(days_data)
            elif isinstance(days_data, str):
                # It's already a string, keep it as is
                ret['days'] = days_data
            else:
                # Default to empty string for any other type
                ret['days'] = ''
        else:
            # Default to empty string if no days provided
            ret['days'] = ''

        return ret