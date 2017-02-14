from django.db import models
from pygments.lexers import get_all_lexers
from pygments.styles import get_all_styles
import datetime

LEXERS = [item for item in get_all_lexers() if item[1]]


class Event(models.Model):
    created = models.DateTimeField(default = datetime.datetime.now)
    end_date  = models.DateTimeField(default = None)
    title = models.CharField(max_length=100, blank=True, default='')
    description = models.CharField(max_length=100, blank=True, default='')
    

    class Meta:
        ordering = ('created',)
