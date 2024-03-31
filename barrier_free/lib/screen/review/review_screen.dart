import 'dart:io';

import 'package:barrier_free/component/appBar.dart';
import 'package:barrier_free/const/color.dart';
import 'package:barrier_free/provider/user_provider.dart';
import 'package:barrier_free/services/review_service.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

class ReviewScreen extends StatefulWidget {
  final String poiId;

  const ReviewScreen(
      {super.key, required this.poiId});

  @override
  State<ReviewScreen> createState() => _ReviewScreenState();
}

class _ReviewScreenState extends State<ReviewScreen> {
  final TextEditingController _reviewController = TextEditingController();

  bool _elevatorButtonActive = true;
  File? _image;
  int _remainingChars = 0; //300자 글자수 제한
  Map<String, bool> _feedbackButtonsActiveState = {};

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _reviewController.addListener(_onReviewChanged);

  }

  @override
  void dispose() {
    // TODO: implement dispose
    _reviewController.removeListener(_onReviewChanged);
    _reviewController.dispose();
    super.dispose();
  }

  void _onReviewChanged() {
    // 300 - 입력된 글자 수 = 남은 글자 수
    final int currentLength = _reviewController.text.length;
    setState(() {
      _remainingChars = currentLength;
    });
  }

  Future<void> _pickImage() async {
    final ImagePicker _picker = ImagePicker();
    final XFile? selectedImage =
        await _picker.pickImage(source: ImageSource.gallery);

    if (selectedImage != null) {
      setState(() {
        _image = File(selectedImage.path); // 선택된 이미지 파일
      });
    }
  }

  Widget _buildFeedbackButtons() {
    List<Widget> buttons = [];

    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      children: buttons,
    );
  }

  Widget _buildFeedbackButton({
    required IconData icon,
    required String text,
    required bool active,
    required VoidCallback onTap,
  }) {
    return OutlinedButton.icon(
      icon: Icon(icon, color: active ? mainOrange : mainGray),
      label: Text(text, style: TextStyle(color: active ? mainOrange : mainGray)),
      onPressed: onTap,
      style: OutlinedButton.styleFrom(
        side: BorderSide(color: active ? mainOrange : mainGray), // 테두리 색상
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(30), // 버튼 모서리 둥글게
        ),
      ),
    );
  }

  Future<void> _submitReview() async {
    final userProvider = Provider.of<UserProvider>(context, listen: false);
    final userId = userProvider.userId;

    String? imageUrl;
    if (_image != null) {
      imageUrl = await ReviewService().uploadImage(_image!);
    }
    Map<String, int> feedback = {};
    _feedbackButtonsActiveState.forEach((key, value) {
      feedback[key] = value ? 1 : 0;
    });

    bool isReviewAdded = await ReviewService().addReview(
      poiId: widget.poiId,
      userId: userId!,
      content: _reviewController.text,
      feedback: feedback['value']!,
      imageUrl: imageUrl,
    );

    if (isReviewAdded) {
      //다시 상세 페이지로 이동시키기
      Navigator.pop(context, true);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('리뷰 추가에 실패했습니다.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        title: '배리어프리 리뷰',
      ),
      //여기 배리어프리 시설 리스트 length만큼 뿌려야 댐 => ListView..?
      body: SingleChildScrollView(
        padding: EdgeInsets.all(18.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: <Widget>[
            _buildFeedbackButtons(),
            SizedBox(
              height: 16.0,
            ),
            _buildInputField(hint: '최대 255자 입력', controller: _reviewController),
            _buildImagePicker(
              label: '사진 선택',
              onCameraTap: _pickImage,
              pickedImage: _image,
            ),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: _submitReview,
                    child: Text(
                      '리뷰 작성하기',
                      style: TextStyle(
                          fontWeight: FontWeight.bold, fontSize: 16.0),
                    ),
                    style: ElevatedButton.styleFrom(
                        foregroundColor: Colors.white,
                        backgroundColor: mainOrange,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10.0),
                        )),
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }

  Widget _buildInputField(
      {required String hint, required TextEditingController controller}) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(bottom: 8.0),
          child: Text(
            '내용을 작성해주세요.',
            style: TextStyle(
              fontSize: 16.0,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        TextField(
          controller: controller,
          maxLength: 255,
          maxLines: 5,
          decoration: InputDecoration(
            counterText: '',
            hintText: hint,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10.0),
              borderSide: BorderSide.none, //원래 기본 상태 테두리
            ),
            enabledBorder: OutlineInputBorder(
              // 기본 상태 테두리 색상
              borderRadius: BorderRadius.circular(10.0),
              borderSide: BorderSide(color: mainOrange, width: 2),
            ),
            focusedBorder: OutlineInputBorder(
              // 포커스 상태 테두리 색상
              borderRadius: BorderRadius.circular(10.0),
              borderSide: BorderSide(color: mainOrange, width: 2),
            ),
            counter: Text('$_remainingChars/255'),
          ),
          onChanged: (text) {
            setState(() {
              _remainingChars = text.length;
            });
          },
        ),
      ],
    );
  }

  Widget _buildElevatorButtons() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: <Widget>[
        _buildElevatorButton(
          icon: Icons.thumb_up_outlined,
          //이거 나중에 가져와야됨 배리어프리 시설 항목
          text: '엘레베이터',
          active: _elevatorButtonActive,
          onTap: () {
            setState(() {
              _elevatorButtonActive = true;
            });
          },
        ),
        _buildElevatorButton(
          icon: Icons.thumb_down_outlined,
          //이거 나중에 가져와야됨 배리어프리 시설 항목
          text: '엘레베이터',
          active: !_elevatorButtonActive,
          onTap: () {
            setState(() {
              _elevatorButtonActive = false;
            });
          },
        ),
      ],
    );
  }

  Widget _buildElevatorButton({
    required IconData icon,
    required String text,
    required bool active,
    required VoidCallback onTap,
  }) {
    return ElevatedButton.icon(
      icon: Icon(icon, color: active ? Colors.white : Colors.orange),
      label: Text(text,
          style: TextStyle(color: active ? Colors.white : Colors.orange)),
      onPressed: onTap,
      style: ElevatedButton.styleFrom(
        foregroundColor: mainOrange,
        backgroundColor: active ? mainOrange : Colors.white,
        // Foreground color
        side: BorderSide(color: Colors.orange, width: 2),
        // Border color and width
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
      ),
    );
  }

  Widget _buildImagePicker({
    required String label,
    required VoidCallback onCameraTap,
    File? pickedImage,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(bottom: 8.0), // 텍스트와 입력 필드 사이의 간격
          child: Text(
            '배리어프리 시설 사진을 등록해주세요.',
            style: TextStyle(
              fontSize: 16.0,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        InkWell(
          onTap: onCameraTap,
          child: Container(
            height: 300, // 또는 적절한 높이
            decoration: BoxDecoration(
              border: Border.all(color: mainOrange, width: 2),
              borderRadius: BorderRadius.circular(10),
            ),
            child: pickedImage == null
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        Icon(Icons.photo_camera_outlined, color: mainBlack),
                        Text(label),
                      ],
                    ),
                  )
                : ClipRRect(
                    borderRadius: BorderRadius.circular(10),
                    child: Image.file(
                      pickedImage,
                      width: double.infinity,
                      height: double.infinity,
                      fit: BoxFit.contain,
                    ),
                  ),
          ),
        ),
      ],
    );
  }
}
