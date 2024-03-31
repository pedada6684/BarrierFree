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
  final List<String> barrierFreeItems;

  const ReviewScreen(
      {super.key, required this.poiId, required this.barrierFreeItems});

  @override
  State<ReviewScreen> createState() => _ReviewScreenState();
}

class _ReviewScreenState extends State<ReviewScreen> {
  final TextEditingController _reviewController = TextEditingController();

  bool _elevatorButtonActive = true;
  File? _image;
  int _remainingChars = 0; //300자 글자수 제한
  Map<String, bool> likeState = {};
  Map<String, bool> unlikeState = {};

  String mapSelectedToString(Map<String, bool> items) {
    return items.entries
        .where((entry) => entry.value == true)
        .map((entry) => entry.key)
        .join(', ');
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _reviewController.addListener(_onReviewChanged);
    for (var item in widget.barrierFreeItems) {
      likeState[item] = false;
      unlikeState[item] = false;
    }

    print(widget.barrierFreeItems);
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

  Widget _buildThumbsButton({
    required String item,
    required IconData icon,
    required bool isSelected,
    required VoidCallback onPressed,
  }) {
    return ElevatedButton.icon(
      label: Text(
        item,
        style: TextStyle(fontSize: 16.0, fontWeight: FontWeight.bold),
      ),
      icon: Icon(
        icon,
        color: isSelected ? mainOrange : mainGray,
      ),
      style: ElevatedButton.styleFrom(
        foregroundColor: mainBlack,
        // backgroundColor: isSelected ? mainOrange : Colors.white,
        side: BorderSide(color: mainOrange, width: 2),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(30),
        ),
        elevation: 0,
      ),
      onPressed: onPressed,
    );
  }

  List<Widget> buildFeedbackButtons() {
    List<Widget> buttons = [];
    widget.barrierFreeItems.forEach((item) {
      buttons.add(
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            _buildThumbsButton(
              item: item,
              icon: Icons.thumb_up_outlined,
              isSelected: likeState[item] ?? false,
              onPressed: () {
                setState(() {
                  likeState[item] = !(likeState[item] ?? false);
                  unlikeState[item] = false;
                });
              },
            ),
            _buildThumbsButton(
              item: item,
              icon: Icons.thumb_down_outlined,
              isSelected: unlikeState[item] ?? false,
              onPressed: () {
                setState(() {
                  unlikeState[item] = !(unlikeState[item] ?? false);
                  likeState[item] = false;
                });
              },
            ),
          ],
        ),
      );
    });
    return buttons;
  }

  Future<void> _submitReview() async {
    final userProvider = Provider.of<UserProvider>(context, listen: false);
    final userId = userProvider.userId;

    String? imageUrl;
    if (_image != null) {
      imageUrl = await ReviewService().uploadImage(_image!);
    }

    String likString = mapSelectedToString(likeState);
    String unlikString = mapSelectedToString(unlikeState);

    try {
      bool isSuccess = await ReviewService().addReview(
        poiId: widget.poiId,
        userId: userId!,
        content: _reviewController.text,
        lik: likString.isEmpty ? null : likString,
        unlik: unlikString.isEmpty ? null : unlikString,
        imageUrl: imageUrl,
      );
      if (isSuccess) {
        //다시 상세 페이지로 이동시키기
        Navigator.pop(context, true);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('리뷰 추가에 실패했습니다.')),
        );
      }
    } catch (e) {
      throw Exception('리뷰 등록하다가 에러 발생 $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: CustomAppBar(
        title: '배리어프리 리뷰',
      ),
      //여기 배리어프리 시설 리스트 length만큼 뿌려야 댐 => ListView..?
      body: SingleChildScrollView(
        padding: EdgeInsets.all(18.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: <Widget>[
            ...buildFeedbackButtons(),
            SizedBox(
              height: 16.0,
            ),
            _buildInputField(hint: '최대 255자 입력', controller: _reviewController),
            _buildImagePicker(
              label: '사진 선택',
              onCameraTap: _pickImage,
              pickedImage: _image,
            ),
            SizedBox(height: 16.0,),
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
