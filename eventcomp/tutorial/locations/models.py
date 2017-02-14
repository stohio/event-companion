from django.db import models
from pygments.lexers import get_all_lexers
from pygments.styles import get_all_styles
from events.models import Event 
import datetime

LEXERS = [item for item in get_all_lexers() if item[1]]


class Location(models.Model):
    event_id = models.ForeignKey(Event, related_name='event_locations', on_delete=models.CASCADE, blank=True)
    created = models.DateTimeField(default = datetime.datetime.now)
    title = models.CharField(max_length=100, blank=True, default='')
    description = models.CharField(max_length=100, blank=True, default='')

    class Meta:
        ordering = ('created',)
