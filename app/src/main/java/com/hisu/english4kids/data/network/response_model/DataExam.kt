package com.hisu.english4kids.data.network.response_model

import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.model.exam.ExamQuestion

data class DataExam(
    var exam: List<ExamQuestion>
)