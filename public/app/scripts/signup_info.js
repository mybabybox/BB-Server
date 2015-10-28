$(document).ready(function() {
	babybox = this;
    
    // selection hooks
    this.onChangeParentType = function() {
        var parentType = $("input:radio[name='parent_type']:checked").val();
        if (parentType == 'NA' || typeof parentType  == 'undefined' || parentType == null) {
            $("#num_children_row").hide();
            $("#child1_row").hide()
            $("#child2_row").hide()
            $("#child3_row").hide()
            $("#child4_row").hide()
            $("#child5_row").hide()
        } else {
            $("#num_children_row").show();
            babybox.onChangeNumChildren();
        }
    }
    $("input:radio[name='parent_type']").change(this.onChangeParentType);

    this.onChangeNumChildren = function() {
        var numChildren = $("#num_children").val();
        (numChildren >= 1)? $("#child1_row").show() : $("#child1_row").hide();
        (numChildren >= 2)? $("#child2_row").show() : $("#child2_row").hide();
        (numChildren >= 3)? $("#child3_row").show() : $("#child3_row").hide();
        (numChildren >= 4)? $("#child4_row").show() : $("#child4_row").hide();
        (numChildren >= 5)? $("#child5_row").show() : $("#child5_row").hide();
    }
    $("#num_children").change(this.onChangeNumChildren);
    
    // init state
    this.onChangeNumChildren();
    this.onChangeParentType();
  
    // validations
    $("#signup-info").validate({
        //debug : true,
        rules : {
            parent_firstname : {
                required : true,
                minlength : 2,
                maxlength : 15
            },
            parent_lastname : {
                required : true,
                minlength : 2,
                maxlength : 15
            },
            parent_displayname : {
                required : true,
                minlength : 2,
                maxlength : 15
            },
            parent_birth_year : {
                required : true
            },
            parent_location : {
                required : true
            },
            parent_type: {
                required: true
            },
            bb_gender1 : {
                required: {
                    depends: function(element) {
                        //console.log("bb_gender1: " + ($('#num_children').val() >= 1));
                        return $('#num_children').val() >= 1
                    }
                }
            },
            bb_gender2 : {
                required: {
                    depends: function(element) {
                        //console.log("bb_gender2: " + ($('#num_children').val() >= 2));
                        return $('#num_children').val() >= 2
                    }
                }
            },
            bb_gender3 : {
                required: {
                    depends: function(element) {
                        //console.log("bb_gender3: " + ($('#num_children').val() >= 3));
                        return $('#num_children').val() >= 3
                    }
                }
            },
            bb_gender4 : {
                required: {
                    depends: function(element) {
                        //console.log("bb_gender4: " + ($('#num_children').val() >= 4));
                        return $('#num_children').val() >= 4
                    }
                }
            },
            bb_gender5 : {
                required: {
                    depends: function(element) {
                        //console.log("bb_gender5: " + ($('#num_children').val() >= 5));
                        return $('#num_children').val() >= 5 
                    }
                }
            },
            bb_birth_year1 : {
                required: {
                    depends: function(element) {
                        return $('#num_children').val() >= 1
                    }
                }
            },
            bb_birth_year2 : {
                required: {
                    depends: function(element) {
                        return $('#num_children').val() >= 2
                    }
                }
            },
            bb_birth_year3 : {
                required: {
                    depends: function(element) {
                        return $('#num_children').val() >= 3
                    }
                }
            },
            bb_birth_year4 : {
                required: {
                    depends: function(element) {
                        return $('#num_children').val() >= 4
                    }
                }
            },
            bb_birth_year5 : {
                required: {
                    depends: function(element) {
                        return $('#num_children').val() >= 5 
                    }
                }
            },
            bb_birth_month1 : {
                required: {
                    depends: function(element) {
                        return $('#bb_birth_year1').val() != 0
                    }
                }
            },
            bb_birth_month2 : {
                required: {
                    depends: function(element) {
                        return $('#bb_birth_year2').val() != 0
                    }
                }
            },
            bb_birth_month3 : {
                required: {
                    depends: function(element) {
                        return $('#bb_birth_year3').val() != 0
                    }
                }
            },
            bb_birth_month4 : {
                required: {
                    depends: function(element) {
                        return $('#bb_birth_year4').val() != 0
                    }
                }
            },
            bb_birth_month5 : {
                required: {
                    depends: function(element) {
                        return $('#bb_birth_year5').val() != 0
                    }
                }
            }
        },
        messages : {
            parent_firstname : {
                required : "請填寫您的名字",
                minlength : "名字最小2個字",
                maxlength : "名字最多15個字"
            },
            parent_lastname : {
                required : "請填寫您的姓氏",
                minlength : "姓氏最小2個字",
                maxlength : "姓氏最多15個字"
            },
            parent_displayname : {
                required : "請填寫您的顯示名稱",
                minlength : "顯示名稱最小2個字",
                maxlength : "顯示名稱最多15個字"
            },
            parent_birth_year : {
                required : "請選擇您的出生年份"
            },
            parent_location : {
                required : "請選擇您居住的地區"
            },
            parent_type : {
                required : "請選擇您現在的身份"
            },
            bb_gender1 : {
                required : "請選擇小寶寶的性別"
            },
            bb_gender2 : {
                required : "請選擇小寶寶的性別"
            },
            bb_gender3 : {
                required : "請選擇小寶寶的性別"
            },
            bb_gender4 : {
                required : "請選擇小寶寶的性別"
            },
            bb_gender5 : {
                required : "請選擇小寶寶的性別"
            },
            bb_birth_year1 : {
                required : "請選擇小寶寶的預產期或生日年份"
            },
            bb_birth_year2 : {
                required : "請選擇小寶寶的預產期或生日年份"
            },
            bb_birth_year3 : {
                required : "請選擇小寶寶的預產期或生日年份"
            },
            bb_birth_year4 : {
                required : "請選擇小寶寶的預產期或生日年份"
            },
            bb_birth_year5 : {
                required : "請選擇小寶寶的預產期或生日年份"
            },
            bb_birth_month1 : {
                required : "請選擇小寶寶的預產期或生日月份"
            },
            bb_birth_month2 : {
                required : "請選擇小寶寶的預產期或生日月份"
            },
            bb_birth_month3 : {
                required : "請選擇小寶寶的預產期或生日月份"
            },
            bb_birth_month4 : {
                required : "請選擇小寶寶的預產期或生日月份"
            },
            bb_birth_month5 : {
                required : "請選擇小寶寶的預產期或生日月份"
            }
        },
        errorPlacement: function (error, element) {
            if (element.attr("name") == "parent_type") {
                error.appendTo("#parent_type-error-holder");
            } else if (element.attr("name") == "bb_gender1") {
                error.appendTo("#bb_gender1-error-holder");
            } else if (element.attr("name") == "bb_gender2") {
                error.appendTo("#bb_gender2-error-holder");
            } else if (element.attr("name") == "bb_gender3") {
                error.appendTo("#bb_gender3-error-holder");
            } else if (element.attr("name") == "bb_gender4") {
                error.appendTo("#bb_gender4-error-holder");
            } else if (element.attr("name") == "bb_gender5") {
                error.appendTo("#bb_gender5-error-holder");
            } else if (element.attr("name") == "bb_birth_year1") {
                error.appendTo("#bb_birth_year1-error-holder");
            } else if (element.attr("name") == "bb_birth_year2") {
                error.appendTo("#bb_birth_year2-error-holder");
            } else if (element.attr("name") == "bb_birth_year3") {
                error.appendTo("#bb_birth_year3-error-holder");
            } else if (element.attr("name") == "bb_birth_year4") {
                error.appendTo("#bb_birth_year4-error-holder");
            } else if (element.attr("name") == "bb_birth_year5") {
                error.appendTo("#bb_birth_year5-error-holder");
            } else if (element.attr("name") == "bb_birth_month1") {
                error.appendTo("#bb_birth_month1-error-holder");
            } else if (element.attr("name") == "bb_birth_month2") {
                error.appendTo("#bb_birth_month2-error-holder");
            } else if (element.attr("name") == "bb_birth_month3") {
                error.appendTo("#bb_birth_month3-error-holder");
            } else if (element.attr("name") == "bb_birth_month4") {
                error.appendTo("#bb_birth_month4-error-holder");
            } else if (element.attr("name") == "bb_birth_month5") {
                error.appendTo("#bb_birth_month5-error-holder");
            } 
            else {
                if (element.attr("type") == "radio") {
                    error.insertBefore(element);
                } else {
                    error.insertAfter(element);
                }
            }
        }
    });
});