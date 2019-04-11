package com.weidlersoftware.upworktest

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class AlbumsWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("open_albums", true)
            val pending = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val views = RemoteViews(context.packageName, R.layout.albums_widget)
            views.setOnClickPendingIntent(R.id.widget_albums, pending)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

