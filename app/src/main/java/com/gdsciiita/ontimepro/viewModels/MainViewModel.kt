package com.gdsciiita.ontimepro.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsciiita.ontimepro.classes.Course
import com.gdsciiita.ontimepro.classes.User
import com.gdsciiita.ontimepro.network.ClassroomApi
import kotlinx.coroutines.launch

enum class ClassroomApiStatus { LOADING, ERROR, DONE }


class MainViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<ClassroomApiStatus>()
    private val _courses = MutableLiveData<List<Course>>()


    // The external immutable LiveData for the request status
    val status: LiveData<ClassroomApiStatus> = _status
    val courses: LiveData<List<Course>> = _courses


    /**
     * Gets Classroom courses information from the Classroom API Retrofit service and updates the
     * [Course] [List] [LiveData].
     */
    fun getClassroomCourses() {
        //coroutine scope for viewModel
        viewModelScope.launch {
            _status.value = ClassroomApiStatus.LOADING
            try {
                Log.d(TAG, "GETTING COURSES")
                val courseList =  ClassroomApi.retrofitService
                    .getCourses(User.authToken, "ACTIVE", 10, User.userEmail).courseList
                //TODO: REMOVE TEST DATA
                for(course in courseList) {
                    course.facultyName = "Mohammed Javed"
                    course.classType = "Lecture"
                }
                _courses.value = courseList
                _status.value = ClassroomApiStatus.DONE

            } catch (e: Exception) {
                e.message?.let { Log.e("WRONG", it) }
                _status.value = ClassroomApiStatus.ERROR
                _courses.value = listOf()
            }
        }
    }
}
