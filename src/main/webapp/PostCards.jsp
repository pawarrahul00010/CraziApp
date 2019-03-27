<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>   
<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  <title>SB Admin 2 - Blank</title>

  <!-- Custom fonts for this template-->
  <link href="${contextPath}/resources/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <link href="${contextPath}/resources/css/sb-admin-2.min.css" rel="stylesheet">

<style>
	#imagePreview img {
    margin: 10px;
    width: 20%;
    border: 2px solid #333;
}
</style>

</head>

<body id="page-top">

  <!-- Page Wrapper -->
  <div id="wrapper">

    <!-- Sidebar -->
 
		<%@include file="topbar.jsp" %>
		
		   <!--  End of Sidebar  -->

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

      <!-- Main Content -->
      <div id="content">

        <!-- Topbar -->
		<%@include file="navbar.jsp" %>
        <!-- End of Topbar -->

        <!-- Begin Page Content -->
      
        <div class="container-fluid">
          <!-- Page Heading -->
          <h1 class="h3 mb-4 text-gray-800">Post Card Catagory Gallery</h1>
            
            <div class="row">
                
                <div class="col-lg-12">
                    <div class="card shadow mb-4">
                    <div class="card-header py-3">
                   
                      <h6 class="m-0 font-weight-bold text-primary">select Card catagory</h6>
                    </div>
                    <div class="card-body">
                      <p>Upload Card.</p>
                      
                      <div class="row">
                        <div class="col-sm-6">
		                     <form method="POST" action="addCategory1" enctype="multipart/form-data"> 
	                         
	                            <div class="pb-2 pt-2">
		                            <c:forEach items="${cardlist}" var="card">
		                          
		                           		<a href="saveCard1?CardId=${card.cardId}"><img alt="" src="${card.filePath}" with="200" height="200"></a>
		                            
		                            </c:forEach>
	                     
	                          	 </div>
	                     
		                             
		                    </form>
                        </div>
                      
                        <div class="col-sm-6">
	                  	</div>
	                  </div>
                        
                    </div>
                    </div>
                  </div>
                </div>

            </div>

        </div>
        
        <!-- /.container-fluid -->

      </div>
      <!-- End of Main Content -->

      <!-- Footer -->
      <footer class="sticky-footer bg-white">
        <div class="container my-auto">
          <div class="copyright text-center my-auto">
            <span>Copyright &copy; Craziapp 2019</span>
          </div>
        </div>
      </footer>
      <!-- End of Footer -->

    </div>
    <!-- End of Content Wrapper -->

  </div>
  <!-- End of Page Wrapper -->

  <!-- Scroll to Top Button-->
  <a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
  </a>

  <!-- Logout Modal-->
  <div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">Ready to Leave?</h5>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">×</span>
          </button>
        </div>
        <div class="modal-body">Select "Logout" below if you are ready to end your current session.</div>
        <div class="modal-footer">
          <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
          <a class="btn btn-primary" href="login">Logout</a>
        </div>
      </div>
    </div>
  </div>

  <!-- Bootstrap core JavaScript-->
  <script src="${contextPath}/resources/vendor/jquery/jquery.min.js"></script>
  <script src="${contextPath}/resources/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${contextPath}/resources/vendor/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${contextPath}/resources/js/sb-admin-2.min.js"></script>
<script>
        
        var inputLocalFont = document.getElementById("file");
inputLocalFont.addEventListener("change",previewImages,false);

function previewImages(){
  var fileList = this.files;
  var anyWindow = window.URL || window.webkitURL;
  for(var i = 0; i < fileList.length; i++){
    var objectUrl = anyWindow.createObjectURL(fileList[i]);
    $('#imagePreview').append('<img src="' + objectUrl + '" />');
    window.URL.revokeObjectURL(fileList[i]);
  }
}

$('#imagePreview').on('click', 'img', function() {
  var images = $('#imagePreview img').removeClass('selected'),
      img = $(this).addClass('selected');
  
  $('#answer').val(images.index(img));
});

/* $('form').submit(function(e) {
  e.preventDefault();
  alert($('form').serialize());
}); */
        
    </script>
</body>

</html>
