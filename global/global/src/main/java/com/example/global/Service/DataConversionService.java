// package com.example.global.Service;

// import com.example.global.DAO.FourthDAO.FourthDAO;
// import com.example.global.DAO.PrimaryDAO.PrimaryDAO;
// import com.example.global.DAO.SecondaryDAO.SecondaryDAO;
// import com.example.global.DAO.ThirdDAO.ThirdDAO;
// import com.example.global.entity.fourth.Fourth;
// import com.example.global.entity.primary.Primary;
// import com.example.global.entity.second.Secondary;
// import com.example.global.entity.third.Third;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class DataConversionService {

//     public PrimaryDAO convertPrimary(Primary entity) {
//         return new PrimaryDAO(
//             entity.getDateAndTime(),
//             entity.getMillitm(),
//             entity.getTagIndex(),
//             entity.getVal(),
//             entity.getStatus(),
//             entity.getMarker(),
//             "PrimaryDB"
//         );
//     }

//     public SecondaryDAO convertSecondary(Secondary entity) {
//         return new SecondaryDAO(
//             entity.getDateAndTime(),
//             entity.getMillitm(),
//             entity.getTagIndex(),
//             entity.getVal(),
//             entity.getStatus(),
//             entity.getMarker(),
//             "SecondaryDB"
//         );
//     }

//     public ThirdDAO convertThird(Third entity) {
//         return new ThirdDAO(
//             entity.getDateAndTime(),
//             entity.getMillitm(),
//             entity.getTagIndex(),
//             entity.getVal(),
//             entity.getStatus(),
//             entity.getMarker(),
//             "ThirdDB"
//         );
//     }

//     public FourthDAO convertFourth(Fourth entity) {
//         return new FourthDAO(
//             entity.getDateAndTime(),
//             entity.getMillitm(),
//             entity.getTagIndex(),
//             entity.getVal(),
//             entity.getStatus(),
//             entity.getMarker(),
//             "FourthDB"
//         );
//     }

//     public List<PrimaryDAO> convertAllPrimary(List<Primary> entities) {
//         return entities.stream().map(this::convertPrimary).collect(Collectors.toList());
//     }

//     public List<SecondaryDAO> convertAllSecondary(List<Secondary> entities) {
//         return entities.stream().map(this::convertSecondary).collect(Collectors.toList());
//     }

//     public List<ThirdDAO> convertAllThird(List<Third> entities) {
//         return entities.stream().map(this::convertThird).collect(Collectors.toList());
//     }

//     public List<FourthDAO> convertAllFourth(List<Fourth> entities) {
//         return entities.stream().map(this::convertFourth).collect(Collectors.toList());
//     }
// }
