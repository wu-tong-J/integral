$('.details_tab li').click(function(){
	var i=$(this).index();
	$(this).addClass('current').siblings('').removeClass('current');
	$('.board_list').eq(i).show().siblings('board_list').hide()
})

$('#Closep').click(function(){
	$('.prompt_box,.bg').hide()
})

$("#card_btn").click(function(){
	$("#upload").click();
})

$('.tab_change_tit a').click(function(){
	var i=$(this).index();
	$(this).addClass('clicks').siblings('').removeClass('clicks');

})