package com.app.qrscanner.presentation.createQr.calendar

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import com.app.qrscanner.R
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.app.qrscanner.utils.showToast
import com.app.qrscanner.utils.whenNotNull
import it.auron.library.vevent.VEvent
import kotlinx.android.synthetic.main.fragment_create_calendar_code.*
import net.glxn.qrgen.core.scheme.ICal
import net.glxn.qrgen.core.scheme.IEvent
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Wifi
import java.text.SimpleDateFormat
import java.util.*


class CreateCalendarCodeFragment : CreateCodeBaseFragment() {
    override val layoutRes = R.layout.fragment_create_calendar_code
    private var startDate= Calendar.getInstance()
    private var endDate = Calendar.getInstance()
    private val showDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))

    private fun checkInputs(): Boolean {
        if (titleInput.text!!.isEmpty() &&
            locationInput.text.isEmpty() &&
            descriptionInput.text.isEmpty()
        ) {
            getString(R.string.enter_text).showToast(context!!)
            return false
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
    }

    private fun initListeners() {

        startDateText.setText(showDateFormat.format(startDate.time))
        endDateText.setText(showDateFormat.format(endDate.time))

        initDates()

        alldaySwitch.setOnCheckedChangeListener { _, b ->
            if(b) {
                startDateText.setText(showDateFormat.format(startDate.time))
                startTextView.text = "Дата"

                endDateText.setText(showDateFormat.format(startDate.time))
                endDateText.visibility = View.GONE
                endTextView.visibility = View.GONE
            }
            else {
                startTextView.text = "Начало"
                endDateText.visibility = View.VISIBLE
                endTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun initDates() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        startDateText.setOnClickListener {
            val dpd = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear] = dayOfMonth
                    startDate = calendar
                    val formatedDate = showDateFormat.format(calendar.time)
                    startDateText.setText(formatedDate)

                    if(alldaySwitch.isChecked)  endDateText.setText(formatedDate)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        endDateText.setOnClickListener {
            val dpd = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear] = dayOfMonth
                    endDate = calendar
                    val formatedDate = showDateFormat.format(calendar.time)
                    //endDate = calendar.time
                    endDateText.setText(formatedDate)
                },
                year,
                month,
                day
            )
            dpd.show()
        }
    }


    override fun createCode(): Pair<String, Schema> {
        if (checkInputs()) {
            val vEvent = VEvent()
            whenNotNull(titleInput.text.toString()) {
                vEvent.summary = titleInput.text.toString() + "\n"
            }

            vEvent.dtStart = startDate.timeInMillis.toString()
            vEvent.dtEnd = endDate.timeInMillis.toString()

            whenNotNull(descriptionInput.text.toString()) {
                vEvent.summary += descriptionInput.text.toString()
            }

            whenNotNull(locationInput.toString()) {
                vEvent.location = locationInput.text.toString()
            }
            println("Build string after cration ${vEvent.buildString()}")
            return Pair(vEvent.buildString(), ICal(IEvent()))
        }
        return Pair("", Wifi())
    }
}